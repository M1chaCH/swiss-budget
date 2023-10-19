import {Component, Input, OnInit} from '@angular/core';
import {ColorService} from "../../../services/color.service";

@Component({
  selector: 'app-tag-icon',
  templateUrl: './tag-icon.component.html',
  styleUrls: ['./tag-icon.component.scss']
})
export class TagIconComponent implements OnInit {
  @Input() icon!: string;
  @Input() color: string = "var(--michu-tech-primary)";
  useBlackIcon: boolean = false;

  constructor(
      private colors: ColorService,
  ) {
  }

  ngOnInit(): void {
    this.useBlackIcon = this.colors.hasDarkBetterContrast(this.color);
  }
}
