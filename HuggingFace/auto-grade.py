import atexit
import requests
from flask import Flask, request, jsonify, send_file
import fitz  # PyMuPDF
from reportlab.lib.pagesizes import letter
from reportlab.pdfgen import canvas
from hugchat import hugchat
from hugchat.login import Login

app = Flask(__name__)

# Constants for API URL, email, and password
API_URL = "http://localhost:8080/quizzes"
EMAIL = ""
PASSWD = ""

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
# Function to delete all chats (if needed)
def delete_all_chats():
    # Your implementation to delete chats if required
    pass

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

# Function to fetch quiz questions from an API
def fetch_quiz_questions(quiz_id,headers):
    try:
        response = requests.get(f"{API_URL}/{quiz_id}/questions",headers=headers)
        response.raise_for_status()
        questions = response.json()
        return questions
    except requests.exceptions.RequestException as e:
        print(f"Error fetching quiz questions: {str(e)}")
        return None

# Function to authenticate user
# def authenticate_user_(email, password):
#     # Mocking authentication, replace with actual implementation
#     if email == "" and password == "":
#         return {"token": "mocked_token"}
#     else:
#         return None

# Function to generate PDF with quiz questions and user answers
def generate_quiz_pdf(title, questions, user_answers, grade):
    pdf_filename = f"{title}.pdf"
    c = canvas.Canvas(pdf_filename, pagesize=letter)
    c.setLineWidth(.3)
    c.setFont('Helvetica', 12)

    # Title
    c.drawString(100, 780, title)
    c.line(100, 770, 500, 770)

    # Function to wrap text
    def draw_wrapped_text(c, text, x, y, max_width, line_spacing=15):
        lines = []
        while text:
            if c.stringWidth(text) <= max_width:
                lines.append(text)
                break
            else:
                # Find the maximum number of characters that fit within the width
                for i in range(len(text)):
                    if c.stringWidth(text[:i]) > max_width:
                        # Split text at the last space before max_width
                        split_at = text[:i].rfind(' ')
                        if split_at == -1:
                            split_at = i
                        lines.append(text[:split_at])
                        text = text[split_at:].strip()
                        break
        for line in lines:
            c.drawString(x, y, line)
            y -= line_spacing  # Move y down for the next line

    # Questions
    y_position = 750
    max_width = 400  # Maximum width for wrapped text
    for idx, question in enumerate(questions, start=1):
        draw_wrapped_text(c, f"{idx}. {question}", 100, y_position, max_width)
        y_position -= 35  # Space before answers

        # User's answer
        draw_wrapped_text(c, f"Your Answer: {user_answers[idx - 1]}", 120, y_position, max_width)
        y_position -= 85

        # Check if there's enough space for the next question
        if y_position <= 30:
            c.showPage()  # Start a new page
            y_position = 750  # Reset y_position for the new page

    # Grading
    c.drawString(100, y_position, f"Grade: {grade}/5")
    c.save()
    return pdf_filename

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

# Route to handle grading requests
@app.route('/grade_answers/<quiz_id>', methods=['POST'])
def grade_answers(quiz_id):
    token = authenticate_user("youssefalsaeed@gmail.com", "123")
    if not token:
        return jsonify({'error': 'Failed to authenticate user'}), 401

    # Now you can proceed with authenticated operations
    headers = {'Authorization': f'Bearer {token}'}
    if 'pdf_file' not in request.files or 'answers' not in request.form:
        return jsonify({'error': 'Please provide a file in pdf_file and answers in answers'}), 400

    pdf_file = request.files['pdf_file']
    user_answers = request.form.getlist('answers')

    print(user_answers)
    # Extract text from uploaded PDF
    pdf_bytes = pdf_file.read()
    pdf_text = extract_text_from_pdf(pdf_bytes)

    # Fetch quiz questions from API
    questions = fetch_quiz_questions(quiz_id,headers)
    print(questions)
    if not questions:
        return jsonify({'error': 'Failed to fetch quiz questions from API'}), 500

    # Initialize Hugging Face ChatBot and login
    cookie_path_dir = "./cookies/"
    sign = Login(EMAIL, PASSWD)
    cookies = sign.login(cookie_dir_path=cookie_path_dir, save_cookies=True)

    # Create ChatBot instance
    chatbot = hugchat.ChatBot(cookies=cookies)

    # Generate prompt for the chatbot
    prompt = f"Please Grade my answers based on the PDF content: each question of one point grade my answers out of 5\n\nPDF Content:\n\n{pdf_text}\n\nQuiz Questions:\n\n{questions}\n\nAnswers:\n\n{user_answers}"

    # Interact with chatbot to get responses
    chatbot_response = chatbot.chat(prompt)
    print(chatbot_response)
    if not chatbot_response:
        return jsonify({'error': 'Failed to interact with chatbot'}), 500


    # returns a grade out of 5
    grade = chatbot_response

    # Generate PDF with graded answers
    pdf_filename = generate_quiz_pdf("Graded Quiz", questions, user_answers, grade)

    # Return the PDF file with the graded quiz
    return send_file(pdf_filename, as_attachment=True), 200

if __name__ == '__main__':
    app.run(debug=True)
