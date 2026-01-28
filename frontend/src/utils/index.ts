export const formatPhoneNumber = (phoneNumber = '') => {
    if (phoneNumber && phoneNumber.trim().length === 11) {
        const cleanedNumber = phoneNumber.replace(/\D/g, '');
        return cleanedNumber.replace(/(\d{3})(\d{4})(\d{4})/, '$1 $2 $3');
    }
    return phoneNumber;
}


export const characterLimit = (str?: string, length?: number) => {
    if (!str) return '';
    if (str.length <= (length || 20)) {
        return str;
    }
    return `${str.slice(0, length || 20 - 3)}...`;
}