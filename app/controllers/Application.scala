package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  def javascriptRoutes = Action { implicit request =>
    import routes.javascript._
    import variants.routes.javascript._
    Ok(
      Routes.javascriptRouter("jsRoutes")(
        VariantController.searchVariants
      )
    ).as("text/javascript")
  }
}