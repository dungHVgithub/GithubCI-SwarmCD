const dataWeek = {
    labels: ["Students", "Teachers"],
    datasets: [{
        data: [typeof totalStudentWeek !== "undefined" ? totalStudentWeek : 0,
               typeof totalTeacherWeek !== "undefined" ? totalTeacherWeek : 0],
        backgroundColor: ['#42a5f5', '#66bb6a'],
        borderWidth: 2
    }]
};
const dataMonth = {
    labels: ["Students", "Teachers"],
    datasets: [{
        data: [typeof totalStudentMonth !== "undefined" ? totalStudentMonth : 0,
               typeof totalTeacherMonth !== "undefined" ? totalTeacherMonth : 0],
        backgroundColor: ['#42a5f5', '#66bb6a'],
        borderWidth: 2
    }]
};
function getColorArray(length) {
    const palette = [
        '#ff7043', '#26a69a', '#8d6e63', '#d4e157', '#5c6bc0',
        '#ec407a', '#29b6f6', '#9ccc65', '#789262', '#ffb300'
    ];
    const colors = [];
    for (let i = 0; i < length; i++) {
        colors.push(palette[i % palette.length]);
    }
    return colors;
}

let chart;
function renderChart(type) {
    const ctx = document.getElementById('studentTeacherChart').getContext('2d');
    const data = type === 'month' ? dataMonth : dataWeek;
    if (chart) {
        chart.destroy(); // Destroy trước khi tạo mới
    }
    chart = new Chart(ctx, {
        type: 'doughnut',
        data: data,
        options: {
            responsive: true,
            plugins: {
                legend: { display: true, position: 'bottom' }
            },
            cutout: '70%',
        }
    });
}
document.addEventListener('DOMContentLoaded', function () {
    renderChart('week');
    document.getElementById('timeRangeSelect').addEventListener('change', function () {
        renderChart(this.value);
    });
});

let subjectBarChart; // Đặt ngoài hàm renderSubjectBarChart
function renderSubjectBarChart(type) {
    const ctx = document.getElementById('subjectBarChart').getContext('2d');
    const labels = (type === 'month') ? subjectLabelsMonth : subjectLabelsWeek;
    const data = (type === 'month') ? subjectDataMonth : subjectDataWeek;
    const colors = getColorArray(labels.length);

    const chartData = {
        labels: labels,
        datasets: [{
            label: "Số lượng",
            data: data,
            backgroundColor: colors,
            borderRadius: 8,
        }]
    };

    if (subjectBarChart && typeof subjectBarChart.update === "function") {
        subjectBarChart.data = chartData;
        subjectBarChart.update();
    } else {
        // Nếu đã có chart cũ, phải destroy trước khi tạo mới
        if (subjectBarChart) {
            subjectBarChart.destroy();
        }
        subjectBarChart = new Chart(ctx, {
            type: 'bar',
            data: chartData,
            options: {
                responsive: true,
                plugins: {
                    legend: { display: false },
                    tooltip: { enabled: true }
                },
                scales: {
                    x: { beginAtZero: true },
                    y: { beginAtZero: true, ticks: { stepSize: 1 } }
                }
            }
        });
    }
}

document.addEventListener('DOMContentLoaded', function () {
    renderChart('week');
    document.getElementById('timeRangeSelect').addEventListener('change', function () {
        renderChart(this.value);
    });
    // GỌI THÊM renderSubjectBarChart lần đầu:
    renderSubjectBarChart('week');
    // Gán event cho dropdown đổi tuần/tháng của biểu đồ môn học:
    document.getElementById('subjectTimeRangeSelect').addEventListener('change', function () {
        renderSubjectBarChart(this.value);
    });
});

