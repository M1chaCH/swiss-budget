import {Component, Input} from "@angular/core";
import {TagDto} from "../../../dtos/TransactionDtos";

@Component({
  selector: "app-tag",
  templateUrl: "./tag.component.html",
  styleUrls: ["./tag.component.scss"]
})
export class TagComponent {

  @Input() tag!: TagDto;

  get keywordNames() {
    return this.tag.keywords?.map(k => k.keyword).join(", ");
  }
}
