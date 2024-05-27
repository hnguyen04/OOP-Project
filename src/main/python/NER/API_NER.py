from flask import Flask, request, jsonify
import requests
import spacy
app = Flask(__name__)

# Tải mô hình NLP đã huấn luyện của spaCy
nlp_ner = spacy.load("./src/main/python/NER/model-best")#src\main\python\NER\model-best
@app.route('/predict', methods=['POST'])
def predict():
    data = request.data.decode('utf-8')
    doc = nlp_ner(data)
    ans = {}
    for ent in doc.ents:
        ans[ent.label_] = []
    for ent in doc.ents:
        ans[ent.label_].append((ent.start_char, ent.end_char, ent.text))
    return jsonify({'entities': ans})

# Định nghĩa hàm predict
if __name__ == '__main__':
    app.run(debug=True)
    print("Server end")