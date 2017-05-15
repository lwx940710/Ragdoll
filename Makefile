JFLAGS = -g
JC = javac -encoding UTF-8
JAVA = java
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Main.java Model.java BotView.java CenterView.java MenuBarView.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class

run: default
	$(JAVA) -cp . Main
