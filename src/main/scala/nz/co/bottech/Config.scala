package nz.co.bottech

import java.nio.file.{Path, Paths}
import java.util.regex.Pattern

final case class Config(dir: Path = Paths.get(""),
                        excludeNames: Seq[Pattern] = Vector.empty,
                        excludePaths: Seq[Pattern] = Vector.empty)
