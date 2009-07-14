SpaceRobots -- Copyright (c)2009 Michael Banack <bob5972@banack.net>

SpaceRobots is free software: you can redistribute it and/or modify 
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SpaceRobots is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SpaceRobots.  If not, see <http://www.gnu.org/licenses/>.
---------------------------------------------------------------------
The latest (official) version of SpaceRobots can be obtained from Michael Banack
directly (I'm a nice guy!) or online at <http://www.banack.net/projects/SpaceRobots/>.

This program requires BanackLib and jogl to run.

BanackLib can be obtained from Michael Banack directly or online at <http://www.banack.net/BanackLib/>.
You'll need to stick this in your classpath when you run SpaceRobots.

Jogl can be obtained from 
https://jogl.dev.java.net/servlets/ProjectDocumentList?folderID=9260&expandFolder=9260&folderID=0

There are some vague installation directions buried in here
https://jogl.dev.java.net/source/browse/*checkout*/jogl/trunk/doc/userguide/index.html
but basically just stick jogl.jar and gluegen-rt.jar into your classpath.

You can build SpaceRobots with ant <http://ant.apache.org/> using the supplied build.xml file,
or using your favorite java IDE (or by hand...), just make sure you setup the classpath right.
----------------------------------------------------------------------
Once you've got it all setup, simply go java net.banack.spacerobots.SpaceRobots and enjoy the fun!

Check out the sample fleets in net.banack.spacerobots.fleets to get started,
but basically you wanna extend AIFleet, and go from there.

The AI interface is all done over a socket, so if you're really motivated you can
write your AI in whatever language you want.
Let me know if you get a good protocol written and I'll add it in.

Questions, Comments, !Complaints can be addressed to:
Michael Banack <bob5972@banack.net>
