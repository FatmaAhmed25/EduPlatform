import atexit

from flask import Flask, request, jsonify
import fitz  # PyMuPDF
from hugchat import hugchat
from hugchat.login import Login
import re

app = Flask(__name__)

# Constants for API URL, email, and password
EMAIL = "fatmaahmed2901@gmail.com"
PASSWD = "/2)BvnK/r,x6P5~"
cookie_path_dir = "./cookies/"  # NOTE: trailing slash (/) is required to avoid errors
sign = Login(EMAIL, PASSWD)
cookies = sign.login(cookie_dir_path=cookie_path_dir, save_cookies=True)



chatbot = hugchat.ChatBot(cookies=cookies.get_dict())
models = chatbot.get_available_llm_models()

for model in models:
    print(model.name)

chatbot.switch_llm(1)

chatbot.new_conversation(switch_to = True)
# Function to delete all chats (if needed)
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

# def extract_overall_grade(chatbot_response):
#     # Extract the overall grade from the chatbot response
#     response_text = chatbot_response.text
#     lines = response_text.splitlines()
#     for line in lines:
#         if line.startswith("Overall Grade:"):
#             overall_grade = line.split(":")[1].strip()
#             if "/" in overall_grade:
#               overall_grade = overall_grade.split("/")[0].strip()
#             return overall_grade
#     return None

def extract_grades_from_response(chatbot_response):
    response_text = chatbot_response.text
    grades = []
    # Use regex to find all grades in the format: Grade: <number>
    pattern = re.compile(r"Grade:\s*(\d+\.\d+)")
    matches = pattern.findall(response_text)
    for match in matches:
        grades.append(match)
    return grades


# Route to handle grading requests
@app.route('/grade_answers', methods=['POST'])
def grade_answers():
    if 'pdf_files' not in request.files:
        return jsonify({'error': 'Please provide files in pdf_files'}), 400

    pdf_files = request.files.getlist('pdf_files')
    pdf_texts = [extract_text_from_pdf(pdf_file.read()) for pdf_file in pdf_files]
    combined_pdf_text = "\n\n".join(pdf_texts)

    questions = request.form.get('questions').split('\n')
    user_answers = request.form.get('user_answers').split('\n')
    print(questions)
    print(user_answers)
    prompt = (
        f"Please grade my answers based on the PDF content, Questions, and User Answers. "
        f"Grade each question out of 1 point,with values of range from [0,1] including decimals for partially correct answers\n\n"
        f"PDF Content:\n\n{combined_pdf_text}\n\n"
        f"Quiz Questions:\n\n{questions}\n\n"
        f"User Answers:\n\n{user_answers}\n\n"
    )

    for idx, question in enumerate(questions, start=1):
        prompt += (
            f"Question {idx}: {question}\n"
            f"User Answer: {user_answers[idx - 1]}\n"
            f"Grade: 'your grade for this answer'\n\n"
        )


    # prompt += "Overall Grade: 'actual overall grade'\n"

    print(prompt)
    chatbot_response = chatbot.chat(prompt)
    if not chatbot_response:
        return jsonify({'error': 'Failed to interact with chatbot'}), 500

    print(chatbot_response)

    grades = extract_grades_from_response(chatbot_response)
    if not grades:
        return jsonify({'error': 'Failed to extract grades from chatbot response'}), 500

    print(grades)
    # overall_grade = extract_overall_grade(chatbot_response)
    # if not overall_grade:
    #     return jsonify({'error': 'Failed to extract overall grade from chatbot response'}), 500



    return jsonify({
        # 'overallGrade': float(overall_grade),
        'grades': grades
    }), 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5005, debug=True)


# # Function to generate PDF with quiz questions and user answers
# def generate_quiz_pdf(title, questions, user_answers, grade):
#     pdf_filename = f"{title}.pdf"
#     c = canvas.Canvas(pdf_filename, pagesize=letter)
#     c.setLineWidth(.3)
#     c.setFont('Helvetica', 12)
#
#     # Title
#     c.drawString(100, 780, title)
#     c.line(100, 770, 500, 770)
#
#     # Function to wrap text
#     def draw_wrapped_text(c, text, x, y, max_width, line_spacing=15):
#         lines = []
#         while text:
#             if c.stringWidth(text) <= max_width:
#                 lines.append(text)
#                 break
#             else:
#                 # Find the maximum number of characters that fit within the width
#                 for i in range(len(text)):
#                     if c.stringWidth(text[:i]) > max_width:
#                         # Split text at the last space before max_width
#                         split_at = text[:i].rfind(' ')
#                         if split_at == -1:
#                             split_at = i
#                         lines.append(text[:split_at])
#                         text = text[split_at:].strip()
#                         break
#         for line in lines:
#             c.drawString(x, y, line)
#             y -= line_spacing  # Move y down for the next line
#
#     # Questions
#     y_position = 750
#     max_width = 400  # Maximum width for wrapped text
#     for idx, question in enumerate(questions, start=1):
#         draw_wrapped_text(c, f"{idx}. {question}", 100, y_position, max_width)
#         y_position -= 35  # Space before answers
#
#         # User's answer
#         draw_wrapped_text(c, f"Your Answer: {user_answers[idx - 1]}", 120, y_position, max_width)
#         y_position -= 85
#
#         # Check if there's enough space for the next question
#         if y_position <= 30:
#             c.showPage()  # Start a new page
#             y_position = 750  # Reset y_position for the new page
#
#     # Grading
#     c.drawString(100, y_position, f"Grade: {grade}/5")
#     c.save()
#     return pdf_filename
