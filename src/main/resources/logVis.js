function fetchLogsData() {
    fetch('logs.json')
        .then(response => response.json())
        .then(data => {
            const tableBody = document.querySelector('.table100 tbody');
            tableBody.innerHTML = '';

            data.forEach(log => {
                const row = document.createElement('tr');

                const cellIP = document.createElement('td');
                cellIP.textContent = log.client_ip || 'N/A';
                cellIP.classList.add('column1');

                const cellTimestamp = document.createElement('td');
                cellTimestamp.textContent = log.timestamp || 'N/A';
                cellTimestamp.classList.add('column2');

                const cellRequestType = document.createElement('td');
                cellRequestType.textContent = log.http_method || 'N/A';
                cellRequestType.classList.add('column3');

                const cellStatusCode = document.createElement('td');
                cellStatusCode.textContent = log.status_code || 'N/A';
                cellStatusCode.classList.add('column4');

                const cellAction = document.createElement('td');
                cellAction.textContent = log.action || 'N/A';
                cellAction.classList.add('column5');

                const cellLogLevel = document.createElement('td');
                cellLogLevel.textContent = log.log_level || 'N/A';
                cellLogLevel.classList.add('column6');

                row.appendChild(cellIP);
                row.appendChild(cellTimestamp);
                row.appendChild(cellRequestType);
                row.appendChild(cellStatusCode);
                row.appendChild(cellAction);
                row.appendChild(cellLogLevel);

                tableBody.appendChild(row);
            });
        })
        .catch(error => {
            console.error('Error loading logs data:', error);
        });
}

document.addEventListener('DOMContentLoaded', fetchLogsData);