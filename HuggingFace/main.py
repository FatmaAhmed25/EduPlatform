import requests
from flask import Flask, request, jsonify
from hugchat import hugchat
from hugchat.login import Login
import fitz  # PyMuPDF

app = Flask(__name__)

# Log in to huggingface and grant authorization to huggingchat
EMAIL = ""
PASSWD = ""
cookie_path_dir = "./cookies/"  # NOTE: trailing slash (/) is required to avoid errors
sign = Login(EMAIL, PASSWD)
cookies = sign.login(cookie_dir_path=cookie_path_dir, save_cookies=True)

# Create your ChatBot
chatbot = hugchat.ChatBot(cookies=cookies.get_dict())  # or cookie_path="usercookies/<email>.json"

def extract_text_from_pdf(pdf_bytes):
    pdf_document = fitz.open(stream=pdf_bytes, filetype="pdf")
    text = ""
    for page_num in range(len(pdf_document)):
        page = pdf_document.load_page(page_num)
        text += page.get_text()
    return text

def parse_quiz_from_response(response_text):
    questions = []
    question_lines = response_text.split('\n\n')

    for q_block in question_lines:
        if not q_block.strip():
            continue

        lines = q_block.split('\n')
        question_text = ""
        options = []
        correct_answer = ""

        for line in lines:
            line = line.strip()
            if line.startswith("question:"):
                question_text = line.split("question:")[1].strip()
            elif line.startswith("answer1:"):
                options.append(line.split("answer1:")[1].strip())
            elif line.startswith("answer2:"):
                options.append(line.split("answer2:")[1].strip())
            elif line.startswith("answer3:"):
                options.append(line.split("answer3:")[1].strip())
            elif line.startswith("answer4:"):
                options.append(line.split("answer4:")[1].strip())
            elif line.startswith("correct answer:"):
                correct_answer = line.split("correct answer:")[1].strip()

        if question_text and options and correct_answer:
            correct_answer_index = int(correct_answer[-1]) - 1
            question = {
                "text": question_text,
                "points": 1,  # Assuming each question is worth 1 point
                "answers": []
            }

            for idx, option_text in enumerate(options):
                is_correct = (idx == correct_answer_index)
                answer = {
                    "text": option_text,
                    "correct": is_correct
                }
                question["answers"].append(answer)

            questions.append(question)

    return questions

def authenticate_user(email, password):
    url = "http://localhost:8080/auth/authenticate-users"
    body = {
        "email": email,
        "password": password
    }
    try:
        response = requests.post(url, json=body)
        response.raise_for_status()
        return response.json()["token"]
    except requests.exceptions.RequestException as e:
        print(f"Authentication error: {str(e)}")
        return None

@app.route('/chat', methods=['POST'])
def chat():
    if 'prompt' not in request.form or 'pdf_file' not in request.files:
        return jsonify({'error': 'Please provide both prompt and pdf_file'}), 400

    prompt = request.form['prompt']
    pdf_file = request.files['pdf_file']

    # Read the PDF file bytes
    pdf_bytes = pdf_file.read()

    # Extract text from the PDF using PyMuPDF (fitz)
    pdf_text = extract_text_from_pdf(pdf_bytes)

    # Combine prompt and PDF text for the chatbot context
    full_prompt = f"{prompt}\n\nContext from PDF:\n{pdf_text}"

    # Get the chat response
    message_result = chatbot.chat(full_prompt)
    response_text = message_result.wait_until_done()

    # Print the model response for debugging
    print("Model Response:")
    print(response_text)

    # Parse the response into quiz format
    questions = parse_quiz_from_response(response_text)

    # Authenticate user and get token
    token = authenticate_user("youssefalsaeed@gmail.com", "123")
    if not token:
        return jsonify({'error': 'Failed to authenticate user'}), 401

    # Prepare headers with Bearer token
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }

    # Prepare payload for /quizzes API endpoint
    quiz_payload = {
        "title": "new Quiz",
        "startTime": "2024-06-23T07:25:24.952Z",
        "endTime": "2024-06-23T07:25:24.952Z",
        "totalGrade": len(questions),  # Total points assuming 1 point per question
        "courseId": 1,  # Replace with your actual course ID
        "questions": questions
    }

    # Print quiz payload for debugging
    print("Quiz Payload:")
    print(quiz_payload)

    # Send POST request to create the quiz with authentication token
    try:
        response = requests.post("http://localhost:8080/quizzes", json=quiz_payload, headers=headers)
        response.raise_for_status()
        return jsonify({'success': 'Quiz created successfully'}), 200
    except requests.exceptions.RequestException as e:
        return jsonify({'error': f'Error creating quiz: {str(e)}'}), 500

if __name__ == '__main__':
    app.run(debug=True)