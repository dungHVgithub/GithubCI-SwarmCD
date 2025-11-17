// Tạo thêm particles động
        function createParticles() {
            const particlesContainer = document.querySelector('.bg-particles');
            for (let i = 0; i < 20; i++) {
                const particle = document.createElement('div');
                particle.className = 'particle';
                particle.style.left = Math.random() * 100 + '%';
                particle.style.top = Math.random() * 100 + '%';
                particle.style.animationDelay = Math.random() * 6 + 's';
                particle.style.animationDuration = (Math.random() * 3 + 3) + 's';
                particlesContainer.appendChild(particle);
            }
        }

        // Khởi tạo particles khi trang load
        document.addEventListener('DOMContentLoaded', function() {
            createParticles();

            // Handle logo image load error
            const logoImage = document.querySelector('.logo-image');
            const fallbackIcon = document.querySelector('.fallback-icon');

            if (logoImage) {
                logoImage.addEventListener('error', function() {
                    this.style.display = 'none';
                    fallbackIcon.style.display = 'block';
                });

                logoImage.addEventListener('load', function() {
                    this.style.display = 'block';
                    fallbackIcon.style.display = 'none';
                });
            }
        });

        // Thêm hiệu ứng focus cho input
        document.querySelectorAll('.form-control').forEach(input => {
            input.addEventListener('focus', function() {
                this.parentElement.classList.add('focused');
            });

            input.addEventListener('blur', function() {
                if (!this.value) {
                    this.parentElement.classList.remove('focused');
                }
            });
        });