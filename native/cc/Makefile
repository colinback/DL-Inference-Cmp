CC  	= gcc
CFLAGS	= -Wall -fPIC -O3 -march=native
CXX		= g++
CXXFLAGS= -Wall -O3 -march=native -std=c++11 -L. -lgraph
LDFLAGS	= -shared
OBJDIR	= obj
SUBDIRS = \
       	graph_test_tile/compute__concat_concat_1_concat_2 \
		graph_test_tile

OBJ = \
	$(OBJDIR)/compute__concat_concat_1_concat_2.o \
	$(OBJDIR)/library.o 

default: all

all: main copy cleanup

main: main.cpp common.cpp libgraph.so
	$(CXX) $(CXXFLAGS) -o $@ $<

mkdir:
	@mkdir -p $(OBJDIR)

libgraph.so: $(SUBDIRS)
	@$(CC) -o $@ $(OBJDIR)/*.o $(LDFLAGS)

# $(OBJDIR)/library.o: graph_test_tile/library.c
# 	$(CC) $(CFLAGS) -c -o $@ $<
# $(OBJDIR)/compute__concat_concat_1_concat_2.o: graph_test_tile/compute__concat_concat_1_concat_2/compute__concat_concat_1_concat_2.c
# 	$(CC) $(CFLAGS) -c -o $@ $<

$(SUBDIRS): mkdir
	@$(MAKE) -C $@ --no-print-directory
.PHONY: $(SUBDIRS)

copy:
	@cp -a graph_test_tile/weights/. graph_weights/

# Do not clean until we finished building libgraph.so
cleanup: libgraph.so
	@rm -rf $(OBJDIR)

clean:
	@rm -rf $(OBJDIR) main libgraph.so graph_weights/
