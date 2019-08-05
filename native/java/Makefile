default: all

all: graph.jar cleanup copy main

graph.jar:
	@javac -verbose -d graph_test_tile/bin graph_test_tile/src/com/ibm/dlc/*.java
	@jar cvf graph.jar -C graph_test_tile/bin .

main: graph.jar
	@javac -cp .:graph.jar Main.java

copy: graph.jar
	@cp -a graph_test_tile/bin/weights/. graph_weights/

cleanup:
	@rm -rf graph_test_tile/bin/com/*

clean:
	@rm -rf graph.jar *.class graph_test_tile/bin/com/* graph_weights/

# java -cp .:graph.jar Main