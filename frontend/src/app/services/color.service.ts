import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ColorService {
  public readonly DESIRED_CONTRAST_RATIO = 4.5;

  constructor() {
  }

  hasDarkBetterContrast(backgroundHexColor: string): boolean {
    const rgb = this.hexToRgb(backgroundHexColor);

    const bgLuminance = this.calculateLuminance(rgb);

    const blackLuminance = 0;
    const whiteLuminance = 1;

    // Calculate the contrast ratios
    const contrastWithBlack = (bgLuminance + 0.05) / (blackLuminance + 0.05);
    const contrastWithWhite = (whiteLuminance + 0.05) / (bgLuminance + 0.05);

    return contrastWithBlack > contrastWithWhite;
  }

  hexToRgb(hexColor: string): RgbColor {
    const hex = hexColor.replace(/^#/, '');
    const bigint = parseInt(hex, 16);
    const r = (bigint >> 16) & 255;
    const g = (bigint >> 8) & 255;
    const b = bigint & 255;
    return {r, g, b};
  }

  calculateLuminance(rgb: RgbColor): number {
    // Calculate relative luminance using the formula from WCAG 2.0
    const {r, g, b} = rgb;
    const RsRGB = r / 255;
    const GsRGB = g / 255;
    const BsRGB = b / 255;
    const R = RsRGB <= 0.03928 ? RsRGB / 12.92 : Math.pow((RsRGB + 0.055) / 1.055, 2.4);
    const G = GsRGB <= 0.03928 ? GsRGB / 12.92 : Math.pow((GsRGB + 0.055) / 1.055, 2.4);
    const B = BsRGB <= 0.03928 ? BsRGB / 12.92 : Math.pow((BsRGB + 0.055) / 1.055, 2.4);
    return 0.2126 * R + 0.7152 * G + 0.0722 * B;
  }
}

export type RgbColor = {
  r: number,
  g: number,
  b: number
}
