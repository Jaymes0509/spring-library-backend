import pymysql
from elasticsearch import Elasticsearch, helpers
from elasticsearch.helpers import BulkIndexError
from datetime import datetime

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

        # 連接到本機的 Elasticsearch 節點
        self.es = Elasticsearch("http://localhost:9200")
        print("✅ 已連接到 Elasticsearch")

    def create_index(self):
        # 如果已存在名為 books 的 index，先刪除
        if self.es.indices.exists(index="books"):
            print("⚠️ 偵測到舊的 index 'books'，正在刪除...")
            self.es.indices.delete(index="books")
            print("🗑️ 已刪除舊的 index")

        # 建立新的 index，並定義欄位 mapping
        mapping = {
            "mappings": {
                "properties": {
                    "book_id": {"type": "integer"},
                    "isbn": {"type": "keyword"},  # 用來精確查找
                    "title": {
                        "type": "text",           # 支援全文模糊搜尋
                        "fields": {
                            "keyword": {"type": "keyword"}  # 支援排序與精確比對
                        }
                    },
                    "author": {
                        "type": "text",
                        "fields": {
                            "keyword": {"type": "keyword"}
                        }
                    },
                    "publisher": {
                        "type": "text",
                        "fields": {
                            "keyword": {"type": "keyword"}
                        }
                    },
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

        # 建立新的 index
        self.es.indices.create(index="books", body=mapping)
        print("✅ 已建立新的 index 'books' 並設定 mapping")

    def format_datetime(self, dt):
        if dt is None:
            return None
        if isinstance(dt, datetime):
            return dt.isoformat()
        # MySQL 可能回傳字串 'YYYY-MM-DD HH:MM:SS'
        return str(dt).replace(' ', 'T')

    def sync_data(self):
        with self.db.cursor() as cursor:
            cursor.execute("SELECT * FROM books")
            books = cursor.fetchall()
            print(f"📚 從資料庫中取得 {len(books)} 本書籍")

            actions = []
            for book in books:
                # 日期格式轉換
                book["created_at"] = self.format_datetime(book.get("created_at"))
                book["updated_at"] = self.format_datetime(book.get("updated_at"))
                # is_available 處理
                if book.get("is_available") is None:
                    book["is_available"] = 0
                else:
                    # 轉成 bool
                    book["is_available"] = bool(book["is_available"])
                # 其他欄位可依需求補齊
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
                for error in e.errors[:10]:  # 只印前10筆
                    print(error)

if __name__ == "__main__":
    print("🔧 開始同步程序")
    sync = BookSync()
    sync.create_index()
    sync.sync_data()
    print("🎉 同步完成")
