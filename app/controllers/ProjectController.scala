package controllers

import org.maproulette.controllers.ParentController
import org.maproulette.data.{Challenge, Project}
import org.maproulette.data.dal.{TaskDAL, ProjectDAL}
import org.maproulette.utils.Utils
import play.api.libs.json._
import play.api.mvc.Action

/**
  * @author cuthbertm
  */
object ProjectController extends ParentController[Project, Challenge] {
  override protected val dal = ProjectDAL
  override implicit val tReads: Reads[Project] = Project.projectReads
  override implicit val tWrites: Writes[Project] = Project.projectWrites
  override protected val cWrites: Writes[Challenge] = Challenge.challengeWrites
  override protected val cReads: Reads[Challenge] = Challenge.challengeReads
  override protected val childController = ChallengeController

  def getRandomTasks(projectId: Long,
                     tags: String,
                     limit:Int) = Action {
    Utils.internalServerCatcher { () =>
      Ok(Json.toJson(TaskDAL.getRandomTasksStr(Some(projectId), None, tags.split(",").toList, limit)))
    }
  }
}
