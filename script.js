document.getElementById('searchForm').addEventListener('submit', function(e) {
    e.preventDefault();
    
const exclusionOption = document.getElementById('excludeSearch').value;

    // Сопоставление значения с параметром запроса
    const exclusionParam = {
        'all': 'None',
        'branches': 'ExcludeBranchLocations',
        'inactive': 'ExcludeInactive',
        'large': 'ExcludeLargeCompanies',
        'medium': 'ExcludeMediumCompanies',
        'small': 'ExcludeSmallCompanies',
        'corporations': 'ExcludeVeryLargeCompanies'
    }[exclusionOption] || 'None';

    const query = {
        "WHERE": [
            {
                "MATCH": {
                    "Criteria": {
                        "Name": document.getElementById('name').value,
                        "City": document.getElementById('city').value,
                        "Country": document.getElementById('country').value,
                        "Address": document.getElementById('address').value,
                        "EmailOrWebsite": document.getElementById('emailOrWebsite').value,
                        "NationalId": document.getElementById('nationalId').value,
                        "PhoneOrFax": document.getElementById('phoneOrFax').value,
                        "PostCode": document.getElementById('postCode').value,
                        "State": document.getElementById('state').value,
                        "Ticker": document.getElementById('ticker').value,
                        "Isin": document.getElementById('isin').value,
                        "OrbisID": document.getElementById('orbisId').value
                        },
       "Options": {
                        "SelectionMode": "normal",
                        "ExclusionFlags": [exclusionParam]
                    }
                }
            }
        ],
        "SELECT": ["NAME", "BVDID","AKA_NAME", "Match.EmailOrWebsite", "NATIONAL_ID", "BO_NAME"]
    };

    const apiToken = "2LK951a1674f439eee11abd50278abee30dc";
    const apiUrl = 'https://api.bvdinfo.com/v1/Orbis/Companies/data?Query=' + encodeURIComponent(JSON.stringify(query));

    fetch(apiUrl, {
        headers: { 'ApiToken': apiToken }
    })
    .then(response => response.json())
    .then(data => {
        displayResults(data);
    })
    .catch(error => {
        console.error('Error:', error);
    });
});

function displayResults(data) {
    const resultsDiv = document.getElementById('resultsTable');

    if (!data || !data.Data || data.Data.length === 0) {
        resultsDiv.innerHTML = '<p>No data found.</p>';
        return;
    }

    let tableHtml = '<table><thead><tr><th>BVDID</th><th>NAME</th><th>NAME2</th><th>Beneficial Owners</th><th>NATIONAL_ID</th><th>Email/Web</th></tr></thead><tbody>';
    data.Data.forEach(item => {
        const emailOrWeb = item.MATCH && item.MATCH['0'] ? item.MATCH['0'].EmailOrWebsite : '';
        const nationalId = item.NATIONAL_ID ? item.NATIONAL_ID.join(', ') : '';

        tableHtml += `<tr>
            <td>${item.BVDID || ''}</td>
            <td>${item.NAME || ''}</td>
            <td>${item.AKA_NAME || ''}</td>
            <td>${item.BO_NAME || ''}</td>
            <td>${nationalId}</td>
            <td>${emailOrWeb}</td>
        </tr>`;
    });
    tableHtml += '</tbody></table>';
    resultsDiv.innerHTML = tableHtml;
}
