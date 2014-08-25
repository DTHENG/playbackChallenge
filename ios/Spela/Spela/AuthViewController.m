#import "AuthViewController.h"

// author : Daniel Thengvall

@implementation AuthViewController

// Build authentication view cells
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.row) {
            
        // Spela logo
        case 0:
            return [tableView dequeueReusableCellWithIdentifier:@"logoCell" forIndexPath:indexPath];
            
        // First name cell
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
            
        // Last initial cell
        case 2:
            return [tableView dequeueReusableCellWithIdentifier:@"initialCell" forIndexPath:indexPath];
            
        // Play button cell
        case 3:
            return [tableView dequeueReusableCellWithIdentifier:@"playCell" forIndexPath:indexPath];
    }
    
    // Default cell
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"cell" forIndexPath:indexPath];
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.row) {
            
        // Spela logo
        case 0:
            return 80;
            
        // Everything else
        default:
            return 60;
    }
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.row) {
            
        // User selected play button
        case 3: {
            
            // Confirm inputed data
            if (self.firstName == nil ||
                    self.lastInitial == nil ||
                    [self.firstName isEqualToString:@""] ||
                    [self.lastInitial isEqualToString:@""]) {
                [self.tableView deselectRowAtIndexPath:indexPath animated:YES];
                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Please enter your first name and last initial" message:nil delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
                [alert show];
                break;
            }
            
            // Attempt server authentication
            if ([self authenticate:self.firstName :self.lastInitial]) {
                
                // Save user data to disk
                NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
                [prefs setObject:[NSNumber numberWithBool:YES] forKey:@"auth"];
                [prefs setObject:self.firstName forKey:@"firstName"];
                [prefs setObject:self.lastInitial forKey:@"lastInitial"];
                [prefs synchronize];
                
                // Display player
                [self dismissViewControllerAnimated:YES completion:nil];
                break;
            }
            
            // Inform the user in the event of an unforeseen
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"An unknown error has occurred" message:nil delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
            [alert show];
        }
    }
}

// Make authentication api request
- (BOOL)authenticate:(NSString *)firstName :(NSString *)lastInitial {
    
    // Build authentication api post parameters
    NSString *post = [NSString stringWithFormat:@"&auth=true&first_name=%@&last_initial=%@&device_id=%@", firstName, lastInitial, @"iPhone"];
    NSData *postData = [post dataUsingEncoding:NSASCIIStringEncoding allowLossyConversion:YES];
    NSString *postLength = [NSString stringWithFormat:@"%lu",(unsigned long)[postData length]];
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
    
    // Set authenication api endpoint
    [request setURL:[NSURL URLWithString:[NSString stringWithFormat:@"http://playback.dtheng.com/api"]]];
    [request setHTTPMethod:@"POST"];
    [request setValue:postLength forHTTPHeaderField:@"Content-Length"];
    [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Current-Type"];
    [request setHTTPBody:postData];
    
    // Execute api request
    NSURLConnection *conn = [[NSURLConnection alloc]initWithRequest:request delegate:self];
    return conn;
}

// The number of cells in this view
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 4;
}

// Executed when user updates first name field
- (IBAction)firstNameChanged:(id)sender {
    UITextField *field = (UITextField *)sender;
    
    // Store value in memory until form has been submitted
    self.firstName = [field text];
}

// Executed when user updates last initial field
- (IBAction)lastInitialChanged:(id)sender {
    UITextField *field = (UITextField *)sender;
    
    // Store value in memory until form has been submitted
    self.lastInitial = [field text];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData*)data { }

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error { }

- (void)connectionDidFinishLoading:(NSURLConnection *)connection { }

@end