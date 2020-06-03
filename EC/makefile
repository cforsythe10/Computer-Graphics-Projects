JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

default: main.class

main.class: CG_hwEC.java
	javac CG_hwEC.java
	jar cvfe CG_hwEC.jar CG_hwEC *class
	chmod +x CG_hwEC.jar
	chmod +x CG_hwEC

CG_hw1: classes $(ARGS)

classes: main.java
	make print-ARGS
	java main $(ARGS)

print-%  : ; @echo $* = $($*)

clean:
	$(RM) *.class
	$(RM) *.jar
