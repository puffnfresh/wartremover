package org.wartremover
package warts

@deprecated("Use TraversableOps.")
object ListOps extends WartTraverser {

  class Op(name: String, error: String) extends WartTraverser {
    override lazy val className = "org.wartremover.warts.ListOps"

    def apply(u: WartUniverse): u.Traverser = {
      import u.universe._

      val symbol = rootMirror.staticClass("scala.collection.immutable.List")
      val Name: TermName = name
      new u.Traverser {
        override def traverse(tree: Tree): Unit = {
          tree match {
            // Ignore trees marked by SuppressWarnings
            case t if hasWartAnnotation(u)(t) =>
            case Select(left, Name) if left.tpe.baseType(symbol) != NoType =>
              u.error(tree.pos, error)
            // TODO: This ignores a lot
            case LabelDef(_, _, rhs) if isSynthetic(u)(tree) =>
            case _ =>
              super.traverse(tree)
          }
        }
      }
    }
  }

  def apply(u: WartUniverse): u.Traverser =
    WartTraverser.sumList(u)(List(
      new Op("head", "head is disabled - use headOption instead"),
      new Op("tail", "tail is disabled - use drop(1) instead"),
      new Op("init", "init is disabled - use dropRight(1) instead"),
      new Op("last", "last is disabled - use lastOption instead"),
      new Op("reduce", "reduce is disabled - use reduceOption or fold instead"),
      new Op("reduceLeft", "reduceLeft is disabled - use reduceLeftOption or foldLeft instead"),
      new Op("reduceRight", "reduceRight is disabled - use reduceRightOption or foldRight instead")
    ))

}
