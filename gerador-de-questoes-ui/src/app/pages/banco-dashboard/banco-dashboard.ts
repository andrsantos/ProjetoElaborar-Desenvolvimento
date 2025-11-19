import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-banco-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './banco-dashboard.html',
  styleUrls: ['./banco-dashboard.scss']
})
export class BancoDashboard { }