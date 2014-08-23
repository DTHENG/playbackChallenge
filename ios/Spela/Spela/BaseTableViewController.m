//
//  BaseTableViewController.m
//  Spela
//
//  Created by Daniel Thengvall on 8/21/14.
//  Copyright (c) 2014 Daniel Thengvall. All rights reserved.
//

#import "BaseTableViewController.h"
#import "AuthViewController.h"

@implementation BaseTableViewController

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    
    NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
    if ([prefs objectForKey:@"auth"] == nil &&
            ! [self isKindOfClass:[AuthViewController class]]) {
        [self performSegueWithIdentifier:@"authSegue" sender:self];
        return;
    }
    
    if ([self isKindOfClass:[AuthViewController class]]) return;
    
    NSData *urlData;
    NSURLResponse *response;
    NSError *error;
    
    NSString *url = [NSString stringWithFormat:@"http://playback.dtheng.com/api?user=%@%@", [prefs objectForKey:@"firstName"], [prefs objectForKey:@"lastInitial"]];
    urlData = [NSURLConnection sendSynchronousRequest:[[NSURLRequest alloc] initWithURL:
                                                       [[NSURL alloc] initWithString:
                                                        url]]
                                    returningResponse:&response
                                                error:&error];
    if (urlData != nil) {
        NSError *jsonParsingError = nil;
        NSDictionary *response = [NSJSONSerialization JSONObjectWithData:urlData options:0 error:&jsonParsingError];
        
        if (response == nil) {
            [prefs removeObjectForKey:@"auth"];
            [prefs removeObjectForKey:@"user"];
            [self performSegueWithIdentifier:@"authSegue" sender:self];
            return;
        }
        NSLog(@"%@",response);
        
        NSLog(@"current artist: %@", [[response objectForKey:@"current"] objectForKey:@"artist"]);
    }

    
}
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 0;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"cell" forIndexPath:indexPath];
    return cell;
}

@end
