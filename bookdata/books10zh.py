from faker import Faker
import random
import mysql.connector
from tqdm import tqdm  # ✅ 加入進度條函式庫

# 1. 資料庫連線設定
db_config = {
    "host": "localhost",
    "port": 3308,
    "user": "root",
    "password": "uTXa3ZqJ1DMHR9WypFboLgYcKsvfNe58",
    "database": "library"
}

# 2. 初始化 Faker（中文）
fake = Faker(locale="zh_TW")
used_isbns = set()

def generate_unique_isbn():
    while True:
        isbn = "978" + ''.join([str(random.randint(0, 9)) for _ in range(10)])
        if isbn not in used_isbns:
            used_isbns.add(isbn)
            return isbn

def generate_natural_chinese_title():
    sentence = fake.sentence(nb_words=4)  # 產生約4個詞的句子
    title = sentence.rstrip("。．.")       # 去除中文及英文句點
    return title

# 3. 建立資料庫連線
conn = mysql.connector.connect(**db_config)
cursor = conn.cursor()

# 4. SQL 插入語法
sql = """
    INSERT INTO books (isbn, title, author, publisher, publishdate, version, language, is_available)
    VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
"""

# ✅ 5. 產生並寫入 10 萬筆資料（含進度條）
for _ in tqdm(range(100000), desc="Inserting books", unit="筆"):
    data = (
        generate_unique_isbn(),
        generate_natural_chinese_title(),
        fake.name().replace("'", ""),
        fake.company().replace("'", ""),
        random.randint(1980, 2024),
        f"第{random.randint(1, 5)}版",
        "中文",
        1
    )
    cursor.execute(sql, data)

# 6. 提交並關閉連線
conn.commit()
cursor.close()
conn.close()

print("✅ 已成功寫入 10 萬筆自然中文書名的書籍資料！")
