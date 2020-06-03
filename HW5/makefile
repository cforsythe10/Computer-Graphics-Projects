JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

default: main.class

main.class: CG_hw5.java
	javac CG_hw5.java
	jar cvfe CG_hw5.jar CG_hw5 *class
	chmod +x CG_hw5.jar
	chmod +x CG_hw5

CG_hw1: classes $(ARGS)

classes: main.java
	make print-ARGS
	java main $(ARGS)

print-%  : ; @echo $* = $($*)

clean:
	$(RM) *.class
	$(RM) *.jar
