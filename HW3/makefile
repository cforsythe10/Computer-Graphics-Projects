JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

default: main.class

main.class: CG_hw3.java
	javac CG_hw3.java
	jar cvfe CG_hw3.jar CG_hw3 *class
	chmod +x CG_hw3.jar
	chmod +x CG_hw3

CG_hw1: classes $(ARGS)

classes: main.java
	make print-ARGS
	java main $(ARGS)

print-%  : ; @echo $* = $($*)

clean:
	$(RM) *.class
	$(RM) *.jar