from flask import Flask, request, jsonify
import json
import requests
import pathlib
import textwrap
import google.generativeai as genai
''' 
gpg --output "D:/Downloads/DS&AI/Team work/PVT/OOP-Project/src/main/python/TrendDetect/Gemini_api_key.txt" --decrypt "D:/Downloads/DS&AI/Team work/PVT/OOP-Project/src/main/python/TrendDetect/Gemini_api_key.txt.gpg"
'''
class TrendDetector:
    def __init__(self):
        self.app = Flask(__name__)
        self.genai = self.configure_genai()
        self.prompt = self.set_prompt()
        self.model = self.load_model()
        self.app.route('/detect', methods=['POST'])(self.detect)

    def configure_genai(self):
        api_key = open("./src/main/python/trend_detect/Gemini_api_key.txt", "r").read()
        genai.configure(api_key=api_key)
        return genai

    def set_prompt(self):
        sys_instruction = '''To complete the task, you need to follow these steps:
                               1. Read the document
                               2. detect trends in articles
                               3. With each trends, explain reasoning by giving exact citations from articles.
                               4. Output a Json file of trends. Each elements include {name of trend, reasoning, citations}: { {trend1, reasoning, citations}, ...  }
                          '''
        persona = "You are an expert in natural language processing. Your task is detecting trends in articles."
        constraints = "The number of trends must not exceed 3. Keep the answer explicit, clear and easy to transfrom to list or JSON format"
        context = "articles or blogs about Blockchain-related technologies"
        examples = '''<Example> INPUT:  'Circular is applying Blockchain technology for logistics' ,
                            OUTPUT :{{'trend': 'Blokchain for logistics',
                                      'reason': Circular company is developing logistics due to blockchain,
                                      'citations': ['Circular is applying Blockchain technology for logistics'] }}
                   </Example>'''
        reasoning = "Explain your reasoning step-by-step"
        responseFormat = "JSON"
        return [persona,  sys_instruction, constraints, context, examples,  reasoning, responseFormat]

    def load_model(self):
        return self.genai.GenerativeModel(model_name="models/gemini-1.5-pro-latest", system_instruction=self.prompt)
    def detect(self):
        # Nhận dữ liệu đầu vào từ yêu cầu
        data = request.data.decode('utf-8')

        response = self.model.generate_content(data)
        ans = response.text.replace('\n', '') # remove escape sequences
        formatted_ans = ans[7:-3] # remove " '''json " at the beginning and " ''' " at the end
        res = json.loads(formatted_ans)
        return jsonify({'trends' : res})

if __name__ == "__main__":
    detector = TrendDetector()
    detector.configure_genai()
    detector.set_prompt()
    detector.load_model()
    detector.app.run(debug=True, port= 7000)
    print("Server end") 
