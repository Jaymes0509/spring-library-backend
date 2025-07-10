import pymysql
from elasticsearch import Elasticsearch, helpers
from elasticsearch.helpers import BulkIndexError
from datetime import datetime
from tqdm import tqdm  # ✅ 匯入進度條套件

class BookSync:
    def __init__(self):
        # 建立 MySQL 連線
        self.db = pymysql.connect(
            host="localhost",
            port=3308,
            user="root",
            password="uTXa3ZqJ1DMHR9WypFboLgYcKsvfNe58",
            database="library",
            charset="utf8mb4",
            cursorclass=pymysql.cursors.DictCursor
        )

        # 連接到 Elasticsearch
        self.es = Elasticsearch(
            "http://localhost:9200",
            basic_auth=("elastic", "elastic")  # ← 替換成你實際的帳號密碼
        )
        print("✅ 已連接到 Elasticsearch")

    def create_index(self):
        if self.es.indices.exists(index="books"):
            print("⚠️ 偵測到舊的 index 'books'，正在刪除...")
            self.es.indices.delete(index="books")
            print("🗑️ 已刪除舊的 index")

        mapping = {
            "mappings": {
                "properties": {
                    "book_id": {"type": "integer"},
                    "isbn": {"type": "keyword"},
                    "title": {
                        "type": "text",
                        "fields": {"keyword": {"type": "keyword"}}
                    },
                    "author": {"type": "text", "fields": {"keyword": {"type": "keyword"}}},
                    "publisher": {"type": "text", "fields": {"keyword": {"type": "keyword"}}},
                    "publishdate": {"type": "integer"},
                    "version": {"type": "keyword"},
                    "type": {"type": "keyword"},
                    "language": {"type": "keyword"},
                    "classification": {"type": "keyword"},
                    "c_id": {"type": "integer"},
                    "is_available": {"type": "boolean"},
                    "created_at": {"type": "date"},
                    "updated_at": {"type": "date"}
                }
            }
        }

        self.es.indices.create(index="books", body=mapping)
        print("✅ 已建立新的 index 'books' 並設定 mapping")

    def format_datetime(self, dt):
        if dt is None:
            return None
        if isinstance(dt, datetime):
            return dt.isoformat()
        return str(dt).replace(' ', 'T')

    def sync_data(self):
        with self.db.cursor() as cursor:
            cursor.execute("SELECT * FROM books")
            books = cursor.fetchall()
            print(f"📚 從資料庫中取得 {len(books)} 本書籍")

            actions = []
            # ✅ 加入 tqdm 進度條
            for book in tqdm(books, desc="📤 同步中", unit="本"):
                book["created_at"] = self.format_datetime(book.get("created_at"))
                book["updated_at"] = self.format_datetime(book.get("updated_at"))
                book["is_available"] = bool(book.get("is_available", 0))

                actions.append({
                    "_index": "books",
                    "_id": book["book_id"],
                    "_source": book
                })

            try:
                helpers.bulk(self.es, actions)
                print(f"🚀 已成功同步 {len(actions)} 筆書籍資料到 Elasticsearch")
            except BulkIndexError as e:
                print("❌ 有文件同步失敗！")
                for error in e.errors[:10]:
                    print(error)

if __name__ == "__main__":
    print("🔧 開始同步程序")
    sync = BookSync()
    sync.create_index()
    sync.sync_data()
    print("🎉 同步完成")
