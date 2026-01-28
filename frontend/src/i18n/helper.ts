import type {LocaleType} from "/@/i18n/index.ts";

export const loadLocalePool: LocaleType[] = [];

export function setLoadLocalePool(cb: (lp: LocaleType[]) => void) {
    cb(loadLocalePool);
}
