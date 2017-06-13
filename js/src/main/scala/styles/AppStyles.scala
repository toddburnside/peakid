package styles

import scalacss.DevDefaults._

object AppStyles extends StyleSheet.Inline {
  import dsl._

  style(unsafeRoot("body")(
    paddingTop(70.px)))
}
