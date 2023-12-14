import cx_Oracle
import pandas as pd
from kafka import KafkaProducer
from sklearn.preprocessing import LabelEncoder
import tensorflow as tf
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense
import pickle
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score

oracle_username = 'your_username'
oracle_password = 'your_password'
oracle_host = 'your_oracle_host'
oracle_service = 'your_oracle_service'

kafka_server = 'your_kafka_server:9092'
kafka_topic = 'your_kafka_topic'

producer = KafkaProducer(bootstrap_servers=kafka_server)

def fetch_data_and_send_to_kafka():
    oracle_connection = cx_Oracle.connect(oracle_username, oracle_password, f'{oracle_host}/{oracle_service}')
    cursor = oracle_connection.cursor()

    sql_query = 'SELECT * FROM sales_table WHERE selection_condition'

    try:
        while True:
            cursor.execute(sql_query)
            rows = cursor.fetchmany(numRows=1000)

            if not rows:
                break

            df = pd.DataFrame(rows, columns=[col[0] for col in cursor.description])

            for index, row in df.iterrows():
                message = row.to_json().encode('utf-8')
                producer.send(kafka_topic, value=message)

            producer.flush()

    finally:
        cursor.close()
        oracle_connection.close()

fetch_data_and_send_to_kafka()

def preprocess_data():
    consumer = KafkaConsumer(kafka_topic, bootstrap_servers=kafka_server, group_id='consumer_group')

    for message in consumer:
        row = pd.read_json(message.value.decode('utf-8'), typ='series')

        if row['weapon_family'] in ['Family A', 'Family B']:
            row['weapon_family'] = 'Family AB'

        message = row.to_json().encode('utf-8')
        producer.send(kafka_topic_preprocessed, value=message)

        producer.flush()

preprocess_data()

def create_neural_network():
    model = Sequential()

    for _ in range(8):
        model.add(Dense(24, activation='relu'))

    model.add(Dense(number_of_classes, activation='softmax')) 
    model.compile(optimizer='adam', loss='categorical_crossentropy', metrics=['accuracy'])

    return model

def train_neural_network(features, labels):
    neural_network = create_neural_network()
    neural_network.fit(features, labels, epochs=10, batch_size=32, validation_split=0.2)  # Adjust parameters as needed

    return neural_network

trained_model = train_neural_network(features, labels)

with open('path_to_model/model.pkl', 'rb') as file:
    ml_model = pickle.load(file)

accuracy = accuracy_score(y_test, predictions)
precision = precision_score(y_test, predictions)
recall = recall_score(y_test, predictions)
f1 = f1_score(y_test, predictions)

print(f'Accuracy: {accuracy}')
print(f'Precision: {precision}')
print(f'Recall: {recall}')
print(f'F1-Score: {f1}')

ml_model.compile(optimizer='adam', loss='categorical_crossentropy', metrics=['accuracy'])

results = ml_model.evaluate(X_test, y_test)
print(f'Accuracy: {results[1]}')

