import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { AuthService } from '../../services/authService/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    const expectedRole = route.data['expectedRole']; // Use bracket notation to access the property
    const userRole = localStorage.getItem("userType");

    if (this.authService.isLoggedIn() && userRole === expectedRole) {
      return true;
    } else {
      this.router.navigate(['/']); // Redirect to homepage
      return false;
    }
  }
}
