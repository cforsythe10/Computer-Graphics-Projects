JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

default: main.class

main.class: CG_hw4.java
	javac CG_hw4.java
	jar cvfe CG_hw4.jar CG_hw4 *class
	chmod +x CG_hw4.jar
	chmod +x CG_hw4

CG_hw1: classes $(ARGS)

classes: main.java
	make print-ARGS
	java main $(ARGS)

print-%  : ; @echo $* = $($*)

clean:
	$(RM) *.class
	$(RM) *.jar
