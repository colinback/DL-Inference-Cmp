import numpy as np
import tf2onnx
import onnx
import tensorflow as tf
from tensorflow.python.ops import variables as variables_lib
from tf2onnx import utils
from tf2onnx.tfonnx import process_tf_graph, tf_optimize
from tf2onnx import optimizer

# batch_size = N
# seq_length = H
# input_size = W
# hidden_size = C
N, H, W, C = 16, 10, 512, 512

# input shape: (batch_size, seq_length, input_size) 
input = tf.placeholder(dtype=tf.float32, shape=(N, H, W), name="input_1")

# create tensorflow model
lstm_cell = tf.nn.rnn_cell.LSTMCell(num_units=C)
output, _ = tf.nn.dynamic_rnn(lstm_cell, input, dtype=tf.float32)
tf.identity(output, name="output_1")

input_names_with_port = ["input_1:0"]
output_names_with_port = ["output_1:0"]

x_val = np.random.rand(N, H, W).astype(np.float32, copy=False)

graph_def = None

# [[[-0.05846359 -0.06566401  0.02254938 -0.26033643 -0.07923548]]
# [[ 0.04879569  0.04215769 -0.06720451 -0.60583305  0.06223793]]
# [[-0.05626901 -0.06627436  0.00422506 -0.5533649  -0.0767431 ]]]
with tf.Session() as sess:
    # sess.run(tf.global_variables_initializer())
    variables_lib.global_variables_initializer().run()
    expected = sess.run(output, feed_dict={input : x_val})

    # convert variables to constants
    output_name_without_port = [n.split(':')[0] for n in output_names_with_port]
    graph_def = tf.graph_util.convert_variables_to_constants(sess, sess.graph_def, output_name_without_port)

    tf.train.write_graph(graph_def, './models', 'lstm.pb', as_text=False)

    tf.saved_model.simple_save(
        sess,
        "./models/lstm/1",
        inputs={'input_1': input},
        outputs={'output_1': output})

    print('\nSaved model:')

tf.reset_default_graph()
tf.import_graph_def(graph_def, name='')

# [array([[[-0.05846359 -0.06566401  0.02254938 -0.26033643 -0.07923548]],
#         [[ 0.04879569  0.04215769 -0.06720451 -0.60583305  0.06223793]],
#         [[-0.05626901 -0.06627436  0.00422506 -0.5533649  -0.0767431 ]]], dtype=float32)]
with tf.Session() as sess:
    # output_dict: get tensor by output name
    output_dict = []
    for out_name in output_names_with_port:
        output_dict.append(sess.graph.get_tensor_by_name(out_name))

    expected = sess.run(output_dict, feed_dict={"input_1:0" : x_val})

# tf optimize
graph_def = tf_optimize(input_names_with_port, output_names_with_port, sess.graph_def, fold_constant=True)

tf.reset_default_graph()
tf.import_graph_def(graph_def, name='')

# convert to onnx
with tf.Session() as sess:
    g = process_tf_graph(sess.graph, output_names=output_names_with_port)
    g = optimizer.optimize_graph(g)
    model_proto = g.make_model("lstm")
    utils.save_onnx_model("./models", "lstm", feed_dict={"input_1:0" : input}, model_proto=model_proto)
