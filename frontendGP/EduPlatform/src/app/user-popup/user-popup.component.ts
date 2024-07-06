import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
    selector: 'app-user-popup',
    templateUrl: './user-popup.component.html',
    styleUrls: ['./user-popup.component.scss']
})
export class UserPopupComponent implements OnInit {
    newUser: any = {
        username: '',
        email: '',
        password: '',
        userType: ''
    };

    constructor(public dialogRef: MatDialogRef<UserPopupComponent>) {}

    ngOnInit(): void {
    }

    saveUser(): void {
        // You can implement saving logic here or emit this.newUser to parent component
        this.dialogRef.close(this.newUser);
    }
}
