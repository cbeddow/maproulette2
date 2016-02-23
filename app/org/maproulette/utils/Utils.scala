package org.maproulette.utils

import play.api.Logger
import play.api.mvc.Results._
import play.api.libs.json.{Json, JsObject, JsValue}
import play.api.mvc.Result
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._


/**
  * @author cuthbertm
  */
object Utils {

  def internalServerCatcher(block:() => Result) : Result = {
    try {
      block()
    } catch {
      case e:Exception =>
        Logger.error(e.getMessage, e)
        InternalServerError(Json.obj("status" -> "KO", "message" -> e.getMessage))
    }
  }

  /**
    * Quick method that will include a -1 id. -1 is the long value that is used for an object
    * that has not been inserted into the database yet. Will not add if id is already there
    *
    * @param json The json that you want to add the id into
    * @return
    */
  def insertJsonID(json:JsValue) : JsValue = {
    (json \ "id").asOpt[Long] match {
      case Some(_) => json
      case None => json.as[JsObject] + ("id" -> Json.toJson(-1))
    }
  }

  def getCaseClassVariableNames[T:TypeTag] : List[MethodSymbol] = {
    typeOf[T].members.collect {
      case m:MethodSymbol if m.isCaseAccessor => m
    }.toList
  }

  def getCaseClassValueMappings[T:ClassTag](obj:T)(implicit tag:TypeTag[T]) : Map[MethodSymbol, Any] = {
    implicit val mirror = scala.reflect.runtime.currentMirror
    val im = mirror.reflect(obj)
    typeOf[T].members.collect {
      case m:MethodSymbol if m.isCaseAccessor => m -> im.reflectMethod(m).apply()
    }.toMap
  }
}
