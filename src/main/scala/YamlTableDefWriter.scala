package mojoz.metadata.out

import scala.Option.option2Iterable
import scala.annotation.tailrec
import mojoz.metadata._
import mojoz.metadata.io._

class YamlTableDefWriter {
  val MaxLineLength = 100
  private val yamlChA = ":#"
    .toCharArray.map(_.toString).toSet
  private val yamlChB = ",[]{}&*!|>'%@`\""
    .toCharArray.map(_.toString).toSet
  private def escapeYamlValue(s: String) =
    if (s != null &&
      (yamlChA.exists(s contains _) || yamlChB.exists(s startsWith _)))
      "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\""
    else s
  def toYamlColDef(colDef: ColumnDef[ExColumnType]) = {
    import colDef._
    val t = colDef.type_.type_ getOrElse new XsdType(null, None, None, None, false)
    val typeString = List(
      Option(t.name),
      t.length,
      t.totalDigits,
      t.fractionDigits).flatMap(x => x) mkString " "
    val enumString = Option(colDef.enum)
      .filter(_ != null)
      .filter(_ != Nil)
      .map(_.mkString("(", ", ", ")"))
      .getOrElse("")

    val defString = List(
      (name, 20),
      (colDef.type_.nullable map (b => if (b) "?" else "!") getOrElse " ", 1),
      (typeString, 10),
      (enumString, 2))
      .foldLeft(("", 0))((r, t) => (
        List(r._1, t._1).mkString(" ").trim.padTo(r._2 + t._2, " ").mkString,
        r._2 + t._2 + 1))._1

    val hasComment = (comments != null && comments.trim != "")
    val slComment =
      if (hasComment) " : " + escapeYamlValue(comments.trim) else ""
    if (!hasComment) defString.trim
    else if (MaxLineLength >= defString.length + slComment.length)
      (defString + slComment).trim
    else {
      val indent =
        if (MaxLineLength < defString.length + 20) " " * 41
        else " " * (2 + defString.length + 3)
      wrapped(escapeYamlValue(comments.trim), defString + " :", indent)
    }
  }
  def toYaml(entity: TableDef[ExColumnType]): String =
    List(Some(entity.name).map("table:   " + _),
      Option(entity.comments).filter(_ != "").map(c =>
        wrapped(escapeYamlValue(c.trim), "comment:", " " * 9)),
      Some("columns:"),
      Option(entity.cols.map(f => "- " + toYamlColDef(f)).mkString("\n")))
      .flatMap(x => x).mkString("\n")
  def toYamlTableDefs(tableDefs: Seq[TableDef[ExColumnType]]): String =
    tableDefs.map(toYaml).mkString("\n\n")
  private def wrapped(words: String, prefix: String, indent: String) = {
    @tailrec
    def _wrapped(
      words: List[String], lines: List[String], line: String): List[String] =
      words match {
        case word :: tail if line.length + word.length + 1 < MaxLineLength =>
          _wrapped(tail, lines, line + " " + word)
        case word :: tail =>
          _wrapped(tail, line :: lines, indent + word)
        case Nil => (line :: lines).reverse
      }
    _wrapped(words.trim.split("[\\s]+").toList, Nil, prefix).mkString("\n")
  }
}