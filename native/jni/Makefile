CC  	= gcc
CFLAGS	= -Wall -fPIC -O3 -march=native
LDFLAGS	= -shared
OBJDIR	= obj
CLSDIR	= graph_test_tile/bin
SUBDIRS = \
       	graph_test_tile/compute__concat_concat_1_concat_2 \
		graph_test_tile

default: all

all: libgraph_native.so graph.jar cleanup main copy

mkdir:
	@mkdir -p $(OBJDIR)
	@mkdir -p $(CLSDIR)

libgraph_native.so: $(SUBDIRS)
	@$(CC) -o $@ $(OBJDIR)/*.o $(LDFLAGS)

$(SUBDIRS): mkdir
	@$(MAKE) -C $@ --no-print-directory
.PHONY: $(SUBDIRS)

graph.jar: mkdir
	@javac -verbose -d $(CLSDIR) graph_test_tile/src/com/ibm/dlc/*.java
	@jar cvf graph.jar -C $(CLSDIR) .

main: graph.jar
	@javac -cp graph.jar Main.java

copy: graph.jar
	@cp -a graph_test_tile/weights/. graph_weights/

# Do not clean until we finished building libgraph.so
cleanup: libgraph_native.so graph.jar
	@rm -rf $(OBJDIR) $(CLSDIR)

clean:
	@rm -rf libgraph_native.* graph.jar *.class $(CLSDIR) $(OBJDIR) graph_weights/

# java -cp .:graph.jar Main
