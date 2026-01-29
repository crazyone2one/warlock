export const emailRegex = /^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/;
export const phoneRegex = /^\d{11}$/;
// 密码校验，8-32位
export const passwordLengthRegex = /^.{8,32}$/;
// 密码校验，必须包含数字和字母，特殊符号范围校验
export const passwordWordRegex = /^(?=.*\d)(?=.*[a-zA-Z])[0-9a-zA-Z!@#$%^&*()_+.]+$/;

export const validateEmail = (email: string) => {
    return emailRegex.test(email);
}
export const validatePhone = (phone: string) => {
    return phoneRegex.test(phone);
}
export const validatePasswordLength = (value: string) => {
    return passwordLengthRegex.test(value);
}
export const validateWordPassword = (value: string) => {
    return passwordWordRegex.test(value);
}