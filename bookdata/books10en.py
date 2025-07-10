from faker import Faker
import random
import mysql.connector
from tqdm import tqdm  # ✅ 加這行

# 資料庫連線設定
db_config = {
    "host": "localhost",
    "port": 3308,
    "user": "root",
    "password": "uTXa3ZqJ1DMHR9WypFboLgYcKsvfNe58",
    "database": "library"
}

fake = Faker()
used_isbns = set()

def generate_unique_isbn():
    while True:
        isbn = "978" + ''.join([str(random.randint(0, 9)) for _ in range(10)])
        if isbn not in used_isbns:
            used_isbns.add(isbn)
            return isbn

def generate_book_title():
    return fake.sentence(nb_words=4).rstrip('.')

def batch_insert_books(batch_size=1000, total=100000):
    conn = mysql.connector.connect(**db_config)
    cursor = conn.cursor()

    sql = """
        INSERT INTO books (isbn, title, author, publisher, publishdate, version, language, is_available)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
    """

    # ✅ 用 tqdm 包住 range 顯示進度
    for start in tqdm(range(0, total, batch_size), desc="Inserting records", unit="batch"):
        batch_data = []
        for _ in range(batch_size):
            data = (
                generate_unique_isbn(),
                generate_book_title(),
                fake.name().replace("'", ""),
                fake.company().replace("'", ""),
                random.randint(1980, 2024),
                f"Edition {random.randint(1,5)}",
                "English",
                1
            )
            batch_data.append(data)

        cursor.executemany(sql, batch_data)
        conn.commit()

    cursor.close()
    conn.close()
    print("✅ 完成 100,000 筆書籍資料插入")

if __name__ == "__main__":
    batch_insert_books()
