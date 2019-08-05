import onnxruntime as rt
import numpy as np

# input value (batch_size, seq_length, input_size)
x_val = np.random.rand(16, 10, 512).astype(np.float32, copy=False)

# load lstm onnx model
sess = rt.InferenceSession("../../models/lstm.onnx")
input_name = sess.get_inputs()[0].name
label_name = sess.get_outputs()[0].name


def predict(input):
    sess.run([label_name], {input_name: input})[0]

def test_inference(benchmark):
    benchmark.pedantic(predict, kwargs={"input": x_val}, rounds=1000)
