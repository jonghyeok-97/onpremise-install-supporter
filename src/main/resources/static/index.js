let eventSource = null;
let isRunning = false;

const dockerForm = document.getElementById('dockerForm');
const dockerImage = document.getElementById('dockerImage');
const startBtn = document.getElementById('startBtn');
const stopBtn = document.getElementById('stopBtn');
const clearBtn = document.getElementById('clearBtn');
const selectedInfo = document.getElementById('selectedInfo');
const selectedText = document.getElementById('selectedText');
const logContainer = document.getElementById('logContainer');
const statusDot = document.getElementById('statusDot');
const statusText = document.getElementById('statusText');

// 로그 추가 함수
function addLog(message, type = 'info') {
    const timestamp = new Date().toLocaleTimeString('ko-KR');
    const logEntry = document.createElement('div');
    logEntry.className = `log-entry ${type}`;
    logEntry.innerHTML = `<span class="log-timestamp">[${timestamp}]</span>${message}`;

    // 빈 메시지 제거
    const emptyMsg = logContainer.querySelector('.log-empty');
    if (emptyMsg) {
        emptyMsg.remove();
    }

    logContainer.appendChild(logEntry);
    logContainer.scrollTop = logContainer.scrollHeight;
}

// SSE 연결 함수
function connectSSE(imageName) {
    if (eventSource) {
        eventSource.close();
    }

    // 실제 서버 엔드포인트로 변경해야 합니다
    const sseUrl = `/api/docker/pull?image=${encodeURIComponent(imageName)}`;

    addLog(`SSE 연결 시도 중: ${sseUrl}`, 'system');

    eventSource = new EventSource(sseUrl);

    eventSource.onopen = () => {
        statusDot.classList.add('connected');
        statusText.textContent = '연결됨';
        addLog('서버 연결 성공', 'success');
    };

    eventSource.onmessage = (event) => {
        const data = event.data;

        // 로그 타입 파싱 (서버에서 "TYPE: message" 형식으로 보낸다고 가정)
        let logType = 'info';
        let logMessage = data;

        if (data.startsWith('ERROR:')) {
            logType = 'error';
            logMessage = data.substring(6);
        } else if (data.startsWith('SUCCESS:')) {
            logType = 'success';
            logMessage = data.substring(8);
        } else if (data.startsWith('WARNING:')) {
            logType = 'warning';
            logMessage = data.substring(8);
        } else if (data.startsWith('INFO:')) {
            logType = 'info';
            logMessage = data.substring(5);
        }

        addLog(logMessage, logType);
    };

    eventSource.onerror = (error) => {
        statusDot.classList.remove('connected');
        statusDot.classList.add('error');
        statusText.textContent = '연결 오류';
        addLog('서버 연결 오류 발생', 'error');

        if (eventSource) {
            eventSource.close();
            eventSource = null;
        }

        isRunning = false;
        startBtn.disabled = false;
        stopBtn.disabled = true;
    };

    // 완료 이벤트 (서버에서 event: complete로 보내는 경우)
    eventSource.addEventListener('complete', (event) => {
        addLog('작업 완료!', 'success');
        stopSSE();
    });
}

// SSE 중지 함수
function stopSSE() {
    if (eventSource) {
        eventSource.close();
        eventSource = null;
        statusDot.classList.remove('connected', 'error');
        statusText.textContent = '연결 끊김';
        addLog('연결 종료', 'system');
    }

    isRunning = false;
    startBtn.disabled = false;
    stopBtn.disabled = true;
}

// 폼 제출 이벤트
dockerForm.addEventListener('submit', (e) => {
    e.preventDefault();

    const selectedValue = dockerImage.value;
    const selectedOption = dockerImage.options[dockerImage.selectedIndex].text;

    if (!selectedValue) {
        alert('Docker 이미지를 선택해주세요!');
        return;
    }

    // 선택 정보 표시
    selectedText.innerHTML = `
                <strong>이미지:</strong> <code>${selectedValue}</code><br>
                <strong>설명:</strong> ${selectedOption.split(' - ')[1]}
            `;
    selectedInfo.style.display = 'block';

    // 로그 시작
    addLog(`Docker 이미지 pull 시작: ${selectedValue}`, 'system');
    addLog(`명령어: docker pull ${selectedValue}`, 'info');

    // SSE 연결
    connectSSE(selectedValue);

    isRunning = true;
    startBtn.disabled = true;
    stopBtn.disabled = false;
});

// 중지 버튼
stopBtn.addEventListener('click', () => {
    addLog('사용자가 작업을 중지했습니다', 'warning');
    stopSSE();
});

// 로그 지우기 버튼
clearBtn.addEventListener('click', () => {
    logContainer.innerHTML = '<div class="log-empty">로그가 지워졌습니다.<br>새로운 작업을 시작하세요.</div>';
    selectedInfo.style.display = 'none';
});

// 페이지 언로드 시 SSE 연결 종료
window.addEventListener('beforeunload', () => {
    if (eventSource) {
        eventSource.close();
    }
});

document.addEventListener('DOMContentLoaded', () => {
    // loadOSSelectBox();
    loadImageSelectBox();
})

const loadImageSelectBox = async () => {
    const selectElement = document.getElementById('dockerImage');
    const images = await fetchJsonData('GET', '/api/docker/images', null);
    if (images.length === 0) {
        addLog('설치 가능한 기술이 없습니다.', 'warning');
        return;
    }

    // 셀렉트 박스에 추가
    images.forEach(image => {
        const option = document.createElement('option');
        option.value = image.name;
        option.textContent = image.name;
        selectElement.appendChild(option);
    });
}

const fetchJsonData = async (method, url, param) => {
    try {
        const options = {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            }
        };

        // GET 요청이면 query string으로, 아니면 body에 추가
        if (method === 'GET' && param) {
            const queryString = new URLSearchParams(param).toString();
            url = `${url}?${queryString}`;
        } else if (method !== 'GET' && param) {
            options.body = JSON.stringify(param);
        }

        const response = await fetch(url, options);

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }

        return await response.json();
    } catch (error) {
        console.error('fetch 실패 ', error);
        addLog(`에러: ${error.message}`, 'error');
    }
}

// 데모를 위한 시뮬레이션 (실제 서버 없을 때)
// 아래 코드는 서버가 준비되면 삭제하세요
function simulateSSE(imageName) {
    const messages = [
        { delay: 500, type: 'info', msg: `Pulling ${imageName}:latest...` },
        { delay: 1000, type: 'info', msg: 'latest: Pulling from library/' + imageName },
        { delay: 1500, type: 'info', msg: 'Digest: sha256:abc123...' },
        { delay: 2000, type: 'info', msg: 'Status: Downloaded newer image' },
        { delay: 2500, type: 'success', msg: `Successfully pulled ${imageName}:latest` }
    ];

    statusDot.classList.add('connected');
    statusText.textContent = '연결됨 (시뮬레이션)';
    addLog('데모 모드로 실행 중 (실제 서버 연결 아님)', 'warning');

    messages.forEach(({ delay, type, msg }) => {
        setTimeout(() => {
            if (isRunning) {
                addLog(msg, type);
            }
        }, delay);
    });

    setTimeout(() => {
        if (isRunning) {
            stopSSE();
        }
    }, 3000);
}

// 실제 서버가 없을 때 시뮬레이션 사용
// connectSSE 함수 내부를 다음과 같이 변경:
/*
function connectSSE(imageName) {
    simulateSSE(imageName);
}
*/