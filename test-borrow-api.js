// 借閱API測試腳本
// 使用方法: node test-borrow-api.js

const axios = require('axios');

const BASE_URL = 'http://localhost:8080';
let authToken = null;

// 測試用的會員帳號
const TEST_USER = {
    email: 'test@example.com',
    password: 'password123'
};

// 測試用的書籍ID
const TEST_BOOK_ID = 1;

async function login() {
    try {
        console.log('🔐 登入中...');
        const response = await axios.post(`${BASE_URL}/api/auth/login`, TEST_USER);
        
        if (response.data.success) {
            authToken = response.data.token;
            console.log('✅ 登入成功');
            return true;
        } else {
            console.log('❌ 登入失敗:', response.data.message);
            return false;
        }
    } catch (error) {
        console.log('❌ 登入錯誤:', error.response?.data?.message || error.message);
        return false;
    }
}

async function testBorrowBook() {
    try {
        console.log('\n📚 測試借書...');
        const response = await axios.post(`${BASE_URL}/api/borrows/borrow`, {
            bookId: TEST_BOOK_ID
        }, {
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json'
            }
        });
        
        if (response.data.success) {
            console.log('✅ 借書成功:', response.data.data.bookTitle);
            return response.data.data.borrowId;
        } else {
            console.log('❌ 借書失敗:', response.data.message);
            return null;
        }
    } catch (error) {
        console.log('❌ 借書錯誤:', error.response?.data?.message || error.message);
        return null;
    }
}

async function testCheckRenewable(borrowId) {
    try {
        console.log('\n🔄 測試檢查續借資格...');
        const response = await axios.get(`${BASE_URL}/api/borrows/renew/${borrowId}/check`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });
        
        if (response.data.success) {
            console.log('✅ 續借資格檢查成功:', response.data.data.canRenew ? '可續借' : '不可續借');
            return response.data.data.canRenew;
        } else {
            console.log('❌ 續借資格檢查失敗:', response.data.message);
            return false;
        }
    } catch (error) {
        console.log('❌ 續借資格檢查錯誤:', error.response?.data?.message || error.message);
        return false;
    }
}

async function testRenewBook(borrowId) {
    try {
        console.log('\n🔄 測試續借...');
        const response = await axios.post(`${BASE_URL}/api/borrows/renew/${borrowId}`, {}, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });
        
        if (response.data.success) {
            console.log('✅ 續借成功，新到期日:', response.data.data.dueDate);
            return true;
        } else {
            console.log('❌ 續借失敗:', response.data.message);
            return false;
        }
    } catch (error) {
        console.log('❌ 續借錯誤:', error.response?.data?.message || error.message);
        return false;
    }
}

async function testGetCurrentBorrows() {
    try {
        console.log('\n📖 測試獲取當前借閱...');
        const response = await axios.get(`${BASE_URL}/api/borrows/current`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });
        
        if (response.data.success) {
            console.log('✅ 獲取當前借閱成功，共', response.data.data.length, '本');
            response.data.data.forEach(book => {
                console.log(`  - ${book.bookTitle} (${book.status})`);
            });
            return response.data.data;
        } else {
            console.log('❌ 獲取當前借閱失敗:', response.data.message);
            return [];
        }
    } catch (error) {
        console.log('❌ 獲取當前借閱錯誤:', error.response?.data?.message || error.message);
        return [];
    }
}

async function testGetBorrowHistory() {
    try {
        console.log('\n📚 測試獲取借閱歷史...');
        const response = await axios.get(`${BASE_URL}/api/borrows/history`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });
        
        if (response.data.success) {
            console.log('✅ 獲取借閱歷史成功，共', response.data.data.length, '筆記錄');
            return response.data.data;
        } else {
            console.log('❌ 獲取借閱歷史失敗:', response.data.message);
            return [];
        }
    } catch (error) {
        console.log('❌ 獲取借閱歷史錯誤:', error.response?.data?.message || error.message);
        return [];
    }
}

async function testGetBorrowStatistics() {
    try {
        console.log('\n📊 測試獲取借閱統計...');
        const response = await axios.get(`${BASE_URL}/api/borrows/statistics`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });
        
        if (response.data.success) {
            const stats = response.data.data;
            console.log('✅ 獲取借閱統計成功:');
            console.log(`  - 總借閱次數: ${stats.totalBorrows}`);
            console.log(`  - 當前借閱: ${stats.currentBorrows}`);
            console.log(`  - 逾期借閱: ${stats.overdueBorrows}`);
            console.log(`  - 已歸還: ${stats.returnedBorrows}`);
            console.log(`  - 總續借次數: ${stats.totalRenewals}`);
            console.log(`  - 平均借閱天數: ${stats.averageBorrowDuration?.toFixed(1) || 0}天`);
            return stats;
        } else {
            console.log('❌ 獲取借閱統計失敗:', response.data.message);
            return null;
        }
    } catch (error) {
        console.log('❌ 獲取借閱統計錯誤:', error.response?.data?.message || error.message);
        return null;
    }
}

async function testCheckBorrowLimits() {
    try {
        console.log('\n🔒 測試檢查借閱限制...');
        const response = await axios.get(`${BASE_URL}/api/borrows/check-limits`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });
        
        if (response.data.success) {
            const limits = response.data.data;
            console.log('✅ 檢查借閱限制成功:');
            console.log(`  - 當前借閱: ${limits.currentBorrows}/${limits.maxBorrows}`);
            console.log(`  - 逾期書籍: ${limits.overdueBorrows}/${limits.maxOverdueBooks}`);
            console.log(`  - 可借閱: ${limits.canBorrow ? '是' : '否'}`);
            console.log(`  - 有逾期限制: ${limits.hasOverdueLimit ? '是' : '否'}`);
            return limits;
        } else {
            console.log('❌ 檢查借閱限制失敗:', response.data.message);
            return null;
        }
    } catch (error) {
        console.log('❌ 檢查借閱限制錯誤:', error.response?.data?.message || error.message);
        return null;
    }
}

async function testReturnBook(borrowId) {
    try {
        console.log('\n📤 測試還書...');
        const response = await axios.post(`${BASE_URL}/api/borrows/return/${borrowId}`, {}, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });
        
        if (response.data.success) {
            console.log('✅ 還書成功');
            return true;
        } else {
            console.log('❌ 還書失敗:', response.data.message);
            return false;
        }
    } catch (error) {
        console.log('❌ 還書錯誤:', error.response?.data?.message || error.message);
        return false;
    }
}

async function runAllTests() {
    console.log('🚀 開始借閱API測試...\n');
    
    // 1. 登入
    const loginSuccess = await login();
    if (!loginSuccess) {
        console.log('❌ 登入失敗，無法繼續測試');
        return;
    }
    
    // 2. 檢查借閱限制
    await testCheckBorrowLimits();
    
    // 3. 獲取借閱統計
    await testGetBorrowStatistics();
    
    // 4. 獲取當前借閱
    await testGetCurrentBorrows();
    
    // 5. 獲取借閱歷史
    await testGetBorrowHistory();
    
    // 6. 借書
    const borrowId = await testBorrowBook();
    
    if (borrowId) {
        // 7. 檢查續借資格
        const canRenew = await testCheckRenewable(borrowId);
        
        // 8. 如果可以續借，則進行續借
        if (canRenew) {
            await testRenewBook(borrowId);
        }
        
        // 9. 還書
        await testReturnBook(borrowId);
    }
    
    console.log('\n🎉 借閱API測試完成！');
}

// 執行測試
runAllTests().catch(console.error); 