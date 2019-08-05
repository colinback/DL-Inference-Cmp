import tensorflow as tf
import numpy as np

# input value (batch_size, seq_length, input_size)
x_val = np.random.rand(16, 10, 512).astype(np.float32, copy=False)

# load lstm pb model
sess = tf.Session()

print("load tensorflow graph")
f = tf.gfile.GFile("../../models/lstm.pb", "rb")

graph_def = tf.GraphDef()    
graph_def.ParseFromString(f.read())

sess.graph.as_default()
tf.import_graph_def(graph_def, name='')

# output_dict
output_dict = [ sess.graph.get_tensor_by_name("output_1:0") ]

def predict(input):
    sess.run(output_dict, feed_dict={"input_1:0" : input})

def test_inference(benchmark):
    benchmark.pedantic(predict, kwargs={"input": x_val}, rounds=1000)
