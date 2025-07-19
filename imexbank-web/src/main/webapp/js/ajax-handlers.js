// Example AJAX handler for checking NIC/Email availability
// This would be used on the create-customer.jsp page
document.addEventListener('DOMContentLoaded', function () {
    const nicInput = document.getElementById('nicNumber');
    const emailInput = document.getElementById('email');

    const checkAvailability = (element, type) => {
        element.addEventListener('blur', function () {
            const value = element.value;
            if (value) {
                fetch(`/imexbank/customer/checkAvailability?type=${type}&value=${value}`)
                    .then(response => response.json())
                    .then(data => {
                        if (!data.available) {
                            element.classList.add('is-invalid');
                            // You can add a feedback message element here
                        } else {
                            element.classList.remove('is-invalid');
                            element.classList.add('is-valid');
                        }
                    })
                    .catch(error => console.error('Error:', error));
            }
        });
    };

    if (nicInput) {
        checkAvailability(nicInput, 'nic');
    }

    if (emailInput) {
        checkAvailability(emailInput, 'email');
    }
});