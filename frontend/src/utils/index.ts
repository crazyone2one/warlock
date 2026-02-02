import {JSEncrypt} from "jsencrypt";

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

export const formatFileSize = (fileSize: number) => {
    const units = ['B', 'KB', 'MB', 'GB', 'TB'];
    let size = fileSize;
    let unitIndex = 0;

    while (size >= 1024 && unitIndex < units.length - 1) {
        size /= 1024;
        unitIndex++;
    }
    const unit = units[unitIndex];
    if (size) {
        const formattedSize = size.toFixed(2);
        return `${formattedSize} ${unit}`;
    }
    const formattedSize = 0;
    return `${formattedSize} ${unit}`;
}
export const encrypted = (input: string) => {
    const publicKey = localStorage.getItem('salt') || '';
    const encrypt = new JSEncrypt({default_key_size: '1024'});
    encrypt.setPublicKey(publicKey);

    return encrypt.encrypt(input);
}