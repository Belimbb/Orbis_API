let globalBvDID = null;

// Обработка запроса из формы поиска компаний
document.getElementById('companySearchForm').addEventListener('submit', function(e) {
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
	 let criteria = {};

    // Check each field and add it to the criteria object if it's not empty
    if (document.getElementById('name').value) {
        criteria.Name = document.getElementById('name').value;
    }
	if (document.getElementById('city').value) {
    criteria.City = document.getElementById('city').value;
	}
	if (document.getElementById('country').value) {
    criteria.Country = document.getElementById('country').value;
	}
	if (document.getElementById('address').value) {
    criteria.Address = document.getElementById('address').value;
	}
	if (document.getElementById('emailOrWebsite').value) {
    criteria.EmailOrWebsite = document.getElementById('emailOrWebsite').value;
	}
	if (document.getElementById('nationalId').value) {
    criteria.NationalId = document.getElementById('nationalId').value;
	}
	if (document.getElementById('phoneOrFax').value) {
    criteria.PhoneOrFax = document.getElementById('phoneOrFax').value;
	}
	if (document.getElementById('postCode').value) {
    criteria.PostCode = document.getElementById('postCode').value;
	}
	if (document.getElementById('state').value) {
    criteria.State = document.getElementById('state').value;
	}
	if (document.getElementById('ticker').value) {
    criteria.Ticker = document.getElementById('ticker').value;
	}
	if (document.getElementById('isin').value) {
    criteria.Isin = document.getElementById('isin').value;
	}
	if (document.getElementById('orbisId').value) {
    criteria.OrbisID = document.getElementById('orbisId').value;
	}
    // Сбор данных из формы
    const query = {
    "WHERE": [
        {
            "MATCH": {
                "Criteria": criteria,
                "Options": {
                    "SelectionMode": "normal",
                    "ExclusionFlags": [exclusionParam]
                }
            }
        }
    ],
    "SELECT": ["NAME", "BVDID", "AKA_NAME", "Match.EmailOrWebsite", "NATIONAL_ID", "BO_NAME"]
};


    // Отправка запроса к API
	const fullUrl = 'https://api.bvdinfo.com/v1/Orbis/Companies/data?Query=' + encodeURIComponent(JSON.stringify(query));
	console.log("Request URL:", fullUrl);
	fetch(fullUrl, {
    headers: { 'ApiToken': '2LK951a1674f439eee11abd50278abee30dc' }
})
// Rest of the fetch logic...

    .then(response => response.json())
    .then(data => {
        globalBvDID = data.Data.map(item => item.BVDID); // Сохранение BVDID
        displayCompanyResults(data);
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

// Обработка запроса из формы поиска директоров
document.getElementById('directorsSearchForm').addEventListener('submit', function(e) {
    e.preventDefault();

    const query = {
        "WHERE": [
            {"FromCompanies": {"BvDID": globalBvDID}},
            {"Type": ["Individual"]}
        ],
        "SELECT": [
    "CONTACTS_HEADER_BareTitle",
    "CONTACTS_HEADER_Birthdate",
    "CONTACTS_HEADER_FullName",
    "CONTACTS_HEADER_IdDirector",
    "CONTACTS_HEADER_NationalityCountryLabel",
    {
        "MEMBERSHIP_DATA": {
            "FILTERS": "Filter.Name=ContactsFilter;ContactsFilter.CurrentPreviousQueryString=0;ContactsFilter.IfHomeOnlyReturnCountry=1;ContactsFilter.Currents=True;ContactsFilter.SourcesToExcludeQueryString=59B|69B|70B|99B",
            "SELECT": [
                "CONTACTS_MEMBERSHIP_IdCompany",
                "CONTACTS_MEMBERSHIP_NameCompany",
                "CONTACTS_MEMBERSHIP_BeginningNominationDate",
                "CONTACTS_MEMBERSHIP_CurrentOrPreviousStr",
                "CONTACTS_MEMBERSHIP_EndExpirationDate",
                "CONTACTS_MEMBERSHIP_Function"
            ]
        }
    }
]

    };

    const apiToken = "2LK951a1674f439eee11abd50278abee30dc";
    const apiUrl2 = 'https://api.bvdinfo.com/v1/orbis/contacts/data?query=' + encodeURIComponent(JSON.stringify(query));

    fetch(apiUrl2, {
        headers: { 'ApiToken': apiToken }
    })
    .then(response => response.json())
    .then(data => {
        displayDirectorResults(data);
    })
    .catch(error => {
        console.error('Error:', error);
    });
});

function displayDirectorResults(data) {
    const resultsDiv = document.getElementById('directorsResultsTable');

    if (!data || !data.Data || data.Data.length === 0) {
        resultsDiv.innerHTML = '<p>No data found.</p>';
        return;
    }

    let htmlTemplate = `
    <table>
        <tr>
            <th>ID Director</th>
            <th>Full Name</th>
            <th>Birthdate</th>
            <th>Company Name</th>
            <th>Company ID</th>
            <th>Function</th>
            <th>Status</th>
        </tr>
    `;

    data.Data.forEach(item => {
        let memberships = item.MEMBERSHIP_DATA instanceof Array ? item.MEMBERSHIP_DATA : [item.MEMBERSHIP_DATA];
        memberships.forEach(membership => {
            htmlTemplate += `
                <tr>
                    <td>${item.CONTACTS_HEADER_IdDirector}</td>
                    <td>${item.CONTACTS_HEADER_FullName}</td>
                    <td>${formatBirthdate(item.CONTACTS_HEADER_Birthdate)}</td>
                    <td>${membership.CONTACTS_MEMBERSHIP_NameCompany || 'N/A'}</td>
                    <td>${membership.CONTACTS_MEMBERSHIP_IdCompany || 'N/A'}</td>
                    <td>${membership.CONTACTS_MEMBERSHIP_Function || 'N/A'}</td>
                    <td>${membership.CONTACTS_MEMBERSHIP_CurrentOrPreviousStr || 'N/A'}</td>
                </tr>
            `;
        });
    });

    htmlTemplate += `</table>`;
    resultsDiv.innerHTML = htmlTemplate;
}



// Функции для отображения результатов
function displayCompanyResults(data) {
    const resultsDiv = document.getElementById('companyResultsTable');

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


function displayDirectorResults(data) {
    const resultsDiv = document.getElementById('directorsResultsTable');

    if (!data || !data.Data || data.Data.length === 0) {
        resultsDiv.innerHTML = '<p>No data found.</p>';
        return;
    }

    let htmlTemplate = `
    <table>
        <tr>
            <th>ID Director</th>
            <th>Full Name</th>
            <th>Birthdate</th>
            <th>Company Name</th>
            <th>Company ID</th>
            <th>Function</th>
            <th>Status</th>
        </tr>
    `;

    data.Data.forEach(item => {
        let memberships = item.MEMBERSHIP_DATA instanceof Array ? item.MEMBERSHIP_DATA : [item.MEMBERSHIP_DATA];
        memberships.forEach(membership => {
            htmlTemplate += `
                <tr>
                    <td>${item.CONTACTS_HEADER_IdDirector}</td>
                    <td>${item.CONTACTS_HEADER_FullName}</td>
                    <td>${formatBirthdate(item.CONTACTS_HEADER_Birthdate)}</td>
                    <td>${membership.CONTACTS_MEMBERSHIP_NameCompany || 'N/A'}</td>
                    <td>${membership.CONTACTS_MEMBERSHIP_IdCompany || 'N/A'}</td>
                    <td>${membership.CONTACTS_MEMBERSHIP_Function || 'N/A'}</td>
                    <td>${membership.CONTACTS_MEMBERSHIP_CurrentOrPreviousStr || 'N/A'}</td>
                </tr>
            `;
        });
    });

    htmlTemplate += `</table>`;
    resultsDiv.innerHTML = htmlTemplate;
}


// Функция для форматирования даты
function formatBirthdate(dateString) {
    if (!dateString) return 'N/A';

    const dateParts = dateString.split('T')[0].split('-'); // Разделение даты от времени и затем разделение даты на компоненты
    return `${dateParts[2]}.${dateParts[1]}.${dateParts[0]}`; // Переформатирование даты в формат дд.мм.гггг
}

