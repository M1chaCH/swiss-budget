import {Component} from "@angular/core";
import {TagService} from "../../services/tag.service";

@Component({
  selector: "app-home.page",
  templateUrl: "./home.page.component.html",
  styleUrls: ["./home.page.component.scss"]
})
export class HomePageComponent {

  constructor(
      private tags: TagService
  ) {
  }

  reset() {
    this.tags.invalidateCache();
  }
}
