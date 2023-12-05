import {Component, Input, OnInit} from "@angular/core";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {TagDto} from "../../../dtos/TransactionDtos";
import {TagService} from "../../../services/tag.service";
import {ConfirmDialogComponent, ConfirmDialogData} from "../../dialog/confirm-dialog/confirm-dialog.component";
import {DialogService} from "../../dialog/dialog.service";

@Component({
  selector: "app-tag-detail",
  templateUrl: "./tag-detail.component.html",
  styleUrls: ["./tag-detail.component.scss"]
})
export class TagDetailComponent implements OnInit {
  readonly COLOR_CONTROL_NAME = "color";
  readonly ICON_CONTROL_NAME = "icon";
  readonly NAME_CONTROL_NAME = "tag_name";

  @Input() tag!: TagDto;

  formGroup: FormGroup;

  constructor(
      private dialogService: DialogService,
      private tagService: TagService,
      fb: FormBuilder,
  ) {
    this.formGroup = fb.group({
      [this.COLOR_CONTROL_NAME]: ["", [Validators.required]],
      [this.ICON_CONTROL_NAME]: ["", [Validators.required]],
      [this.NAME_CONTROL_NAME]: ["", [Validators.required]],
    });
  }

  ngOnInit() {
    this.formGroup.controls[this.COLOR_CONTROL_NAME].setValue(this.tag.color);
    this.formGroup.controls[this.ICON_CONTROL_NAME].setValue(this.tag.icon);
    this.formGroup.controls[this.NAME_CONTROL_NAME].setValue(this.tag.name);
  }

  save() {
    console.warn("edit not implemented");
  }

  delete() {
    this.dialogService.openDialog<ConfirmDialogData, boolean>(ConfirmDialogComponent, {
      decision: `Do you really want to delete the tag '${this.tag.name}'. All transactions with this tag will loose their connection to the budget and the budget or saving will be deleted!`,
      throttleAccept: true,
    }, (accepted) => {
      if (accepted)
        this.tagService.deleteTag(this.tag.id);
    });
  }
}
