# Named entity recognition model using Spacy 
This model use Spacy to recognize entities in document. The entities will be classified in these tags: 
- PER: person 
- ORG: organization 
- LOC: location
- TIME: time or date
- BLOCKCHAIN: termilogies about Blockchain technology 
- FINANCE: termilogies about finance, such as currency, trading, transaction,...

## Resources 
6 documents are collected randomly from "src\main\resources\data\all_data.csv" and then labeled by team members

## Models: 
The spacy model is created and trained in the .ipynb files. Then, it was saved in folder "model-best". 

## API: 
API is created in API.py
