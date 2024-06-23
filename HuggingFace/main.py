import atexit
import requests
from flask import Flask, request, jsonify, send_file
from hugchat import hugchat
from hugchat.login import Login
import fitz  # PyMuPDF
from reportlab.lib.pagesizes import letter
from reportlab.pdfgen import canvas

app = Flask(__name__)

# Log in to huggingface and grant authorization to huggingchat
EMAIL = ""
PASSWD = ""
cookie_path_dir = "./cookies/"  # NOTE: trailing slash (/) is required to avoid errors
sign = Login(EMAIL, PASSWD)
cookies = sign.login(cookie_dir_path=cookie_path_dir, save_cookies=True)

# Create your ChatBot
chatbot = hugchat.ChatBot(cookies=cookies.get_dict())  # or cookie_path="usercookies/<email>.json"

# Function to delete all conversations
def delete_all_chats():
    chatbot.delete_all_conversations()

# Register the delete_all_chats function to be called on exit
atexit.register(delete_all_chats)

# Function to extract text from PDF
def extract_text_from_pdf(pdf_bytes):
    pdf_document = fitz.open(stream=pdf_bytes, filetype="pdf")
    text = ""
    for page_num in range(len(pdf_document)):
        page = pdf_document.load_page(page_num)
        text += page.get_text()
    return text

# Function to parse quiz questions from response text
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

# Function to authenticate user
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

# Function to generate PDF file with quiz title and questions
def generate_pdf(quiz_title, quiz_questions):
    pdf_filename = f"{quiz_title}.pdf"
    c = canvas.Canvas(pdf_filename, pagesize=letter)
    c.setLineWidth(.3)
    c.setFont('Helvetica', 12)

    # Title
    c.drawString(100, 750, quiz_title)
    c.line(100, 745, 500, 745)

    # Questions
    y_position = 700
    for idx, question in enumerate(quiz_questions, start=1):
        c.drawString(100, y_position, f"{idx}. {question['text']}")
        y_position -= 20  # Space before answers

        for answer in question['answers']:
            c.drawString(120, y_position, f"{'✓' if answer['correct'] else ' '} {answer['text']}")
            y_position -= 15

        y_position -= 10  # Space between questions

        # Check if there's enough space for the next question
        if y_position <= 50:
            c.showPage()  # Start a new page
            y_position = 750  # Reset y_position for the new page

    c.save()
    return pdf_filename


# Flask route for handling POST requests to /chat endpoint
@app.route('/chat', methods=['POST'])
def chat():
    if 'pdf_files' not in request.files:
        return jsonify({'error': 'Please provide one or more files in pdf_files'}), 400

    # Extract form parameters
    courseId = request.form.get('courseId')  # Default to 1 if not provided
    quiz_title = request.form.get('quiz_title')  # Default title if not provided
    startTime = request.form.get('startTime')
    endTime = request.form.get('endTime')


    prompt = """
    Using strictly the following format 
    DO NOT NUMBER THE QUESTIONS
    question: (fill question text here)
    answer1: (fill answer text here)
    answer2: (fill answer text here)
    answer3: (fill answer text here)
    answer4: (fill answer text here)
    correct answer: (type answer1 or answer2 or answer3 or answer4)
    Generate exactly 10 multiple choice questions (MCQs) from the following text. Each answer should be fully specified, Ensure the correct answer is explicitly stated and there are actual answers for each question.
    """

    pdf_files = request.files.getlist('pdf_files')

    # Extract text from all uploaded PDFs
    pdf_texts = []
    for pdf_file in pdf_files:
        pdf_bytes = pdf_file.read()
        pdf_texts.append(extract_text_from_pdf(pdf_bytes))

    combined_pdf_text = "\n\n".join(pdf_texts)

    # Combine prompt and PDF text for the chatbot context
    full_prompt = f"{prompt}\n\nContext from PDF:\n{combined_pdf_text}"

    # Get the chat response
    message_result = chatbot.chat(full_prompt)
    response_text = message_result.wait_until_done()

    # Print the model response for debugging
    print("Model Response:")
    print(response_text)

    # Parse the response into quiz format
    questions = parse_quiz_from_response(response_text)

    # Ensure quiz_title is not None
    if not quiz_title:
        quiz_title = "Quiz Title"

    # Generate PDF file with quiz title and questions
    pdf_filename = generate_pdf(quiz_title, questions)

    # Authenticate user and get token (not changed from your original code)
    token = authenticate_user("youssefalsaeed@gmail.com", "123")
    if not token:
        return jsonify({'error': 'Failed to authenticate user'}), 401

    # Prepare headers with Bearer token (not changed from your original code)
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }

    # Prepare payload for /quizzes API endpoint (not changed from your original code)
    quiz_payload = {
        "title": quiz_title,
        "startTime": startTime,
        "endTime": endTime,
        "totalGrade": len(questions),  # Total points assuming 1 point per question
        "courseId": courseId,
        "questions": questions
    }

    # Print quiz payload for debugging
    print("Quiz Payload:")
    print(quiz_payload)

    # Send POST request to create the quiz with authentication token
    try:
        response = requests.post("http://localhost:8080/quizzes", json=quiz_payload, headers=headers)
        response.raise_for_status()
        return send_file(pdf_filename, as_attachment=True), 200
    except requests.exceptions.RequestException as e:
        return jsonify({'error': f'Error creating quiz: {str(e)}'}), 500

if __name__ == '__main__':
    app.run(debug=True)
