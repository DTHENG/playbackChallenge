//
//  AuthViewController.m
//  Spela
//
//  Created by Daniel Thengvall on 8/21/14.
//  Copyright (c) 2014 Daniel Thengvall. All rights reserved.
//

#import "AuthViewController.h"

@implementation AuthViewController

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 4;
}

- (IBAction)firstNameChanged:(id)sender {
    UITextField *field = (UITextField *)sender;
    self.firstName = [field text];
}

- (IBAction)lastInitialChanged:(id)sender {
    UITextField *field = (UITextField *)sender;
    self.lastInitial = [field text];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.row) {
        case 0:
            return [tableView dequeueReusableCellWithIdentifier:@"logoCell" forIndexPath:indexPath];
        case 1: {
            UITableViewCell *firstName = [tableView dequeueReusableCellWithIdentifier:@"cell" forIndexPath:indexPath];
            
            for (int i = 0; i < [[[firstName contentView] subviews] count]; i++) {
                id thisItem = [[[firstName contentView] subviews] objectAtIndex:i];
                if ([thisItem isKindOfClass:[UITextField class]]) {
                    [thisItem becomeFirstResponder];
                    break;
                }
            }
            return firstName;
        }
        case 2:
            return [tableView dequeueReusableCellWithIdentifier:@"initialCell" forIndexPath:indexPath];
        case 3:
            return [tableView dequeueReusableCellWithIdentifier:@"playCell" forIndexPath:indexPath];
    }
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"cell" forIndexPath:indexPath];
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return indexPath.row == 0 ? 80 : 60;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.row) {
        case 3: {
            if (self.firstName == nil ||
                    self.lastInitial == nil ||
                    [self.firstName isEqualToString:@""] ||
                    [self.lastInitial isEqualToString:@""]) {
                [self.tableView deselectRowAtIndexPath:indexPath animated:YES];
                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Please enter your first name and last initial" message:nil delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
                [alert show];
                break;
            }
            NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
            [prefs setObject:[NSNumber numberWithBool:YES] forKey:@"auth"];
            [prefs setObject:self.firstName forKey:@"firstName"];
            [prefs setObject:self.lastInitial forKey:@"lastInitial"];
            [prefs synchronize];
            [self dismissViewControllerAnimated:YES completion:nil];
        }
    }
}

@end
