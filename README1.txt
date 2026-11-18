@'
from flask import Flask
import mysql.connector
import os

app = Flask(__name__)

@app.route("/")
def home():
    try:
        db = mysql.connector.connect(
            host="db",
            user="root",
            password=os.environ["MYSQL_ROOT_PASSWORD"],
            database=os.environ["MYSQL_DATABASE"]
        )
        cursor = db.cursor()
        cursor.execute("SELECT id, name, course FROM students")
        rows = cursor.fetchall()
        cursor.close()
        db.close()
        table_rows = "".join(f"<tr><td>{r[0]}</td><td>{r[1]}</td><td>{r[2]}</td></tr>" for r in rows)
        return f"""
        <html><body style="font-family:Arial;text-align:center;margin-top:60px">
        <h1>Docker Compose: Flask + MySQL</h1>
        <p style="color:green">Connected to MySQL successfully!</p>
        <table border="1" cellpadding="8" style="margin:auto">
        <tr><th>ID</th><th>Name</th><th>Course</th></tr>
        {table_rows}
        </table>
        </body></html>
        """
    except mysql.connector.Error as e:
        return f"Database connection failed: {e}", 503

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=80)
'@ | Set-Content -Encoding ascii app.py

@'
flask
mysql-connector-python
'@ | Set-Content -Encoding ascii requirements.txt

@'
FROM python:3.12-slim
WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
COPY app.py .
EXPOSE 80
CMD ["python", "app.py"]
'@ | Set-Content -Encoding ascii Dockerfile

@'
CREATE TABLE IF NOT EXISTS students (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50),
  course VARCHAR(50)
);
INSERT INTO students (name, course) VALUES
  ('Arvind', 'DevOps'),
  ('Priya', 'Data Science'),
  ('Rahul', 'Cloud Computing');
'@ | Set-Content -Encoding ascii init.sql
Step 2: Write docker-compose.yml (12 marks)
@'
services:
  web:
    build: .
    ports:
      - "8000:80"
    environment:
      MYSQL_ROOT_PASSWORD: rootpass123
      MYSQL_DATABASE: studentdb
    depends_on:
      db:
        condition: service_healthy

  db:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: rootpass123
      MYSQL_DATABASE: studentdb
    volumes:
      - db_data:/var/lib/mysql
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    healthcheck:
      test: ["CMD-SHELL", "mysqladmin ping -h localhost -uroot -p$$MYSQL_ROOT_PASSWORD --silent"]
      interval: 10s
      timeout: 5s
      retries: 10
      start_period: 30s

volumes:
  db_data:
'@ | Set-Content -Encoding ascii docker-compose.yml

dir
docker compose config
