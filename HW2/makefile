JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

default: main.class

main.class: CG_hw2.java
	javac CG_hw2.java
	jar cvfe CG_hw2.jar CG_hw2 *class
	chmod +x CG_hw2.jar
	chmod +x CG_hw2

CG_hw1: classes $(ARGS)

classes: main.java
	make print-ARGS
	java main $(ARGS)

print-%  : ; @echo $* = $($*)

clean:
	$(RM) *.class
	$(RM) *.jar




