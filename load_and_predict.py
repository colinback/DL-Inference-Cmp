import tensorflow as tf
import numpy as np
import onnxruntime as rt

# input value (batch_size, seq_length, input_size)
x_val = np.random.rand(16, 10, 512).astype(np.float32, copy=False)

# load lstm pb model
with tf.Session() as sess:
    print("load tensorflow graph")
    with tf.gfile.GFile("./models/lstm.pb", "rb") as f:
        graph_def = tf.GraphDef()
    
    graph_def.ParseFromString(f.read())
    sess.graph.as_default()
    tf.import_graph_def(graph_def, name='')

    # output_dict
    output_dict = [ sess.graph.get_tensor_by_name("output_1:0") ]

    expected = sess.run(output_dict, feed_dict={"input_1:0" : x_val})
    print(expected)

# load lstm onnx model
sess = rt.InferenceSession("./models/lstm.onnx")
input_name = sess.get_inputs()[0].name
label_name = sess.get_outputs()[0].name
expected = sess.run([label_name], {input_name: x_val})[0]
print(expected)
    